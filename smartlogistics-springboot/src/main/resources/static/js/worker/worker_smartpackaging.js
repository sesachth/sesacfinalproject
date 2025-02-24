/***************************************************
 * 전역 변수
 ***************************************************/
let stompClient = null;   // STOMP 클라이언트
let progressData = [];    // progressState=0 인 물품 목록
const maxItemsToShow = 5; // 한 번에 표시할 최대 행 개수

/**
 * 1) DOMContentLoaded 시점에 초기화
 */
document.addEventListener("DOMContentLoaded", () => {
	console.log("📌 DOM 로드 완료 → loadWorkerOrders() & connectWebSocket()");
	loadWorkerOrders();
	connectWebSocket();
});

/**
 * 2) 물품대기(progressState=0) 목록을 서버에서 가져오기
 */
function loadWorkerOrders() {
	fetch('/admin/progress/dataAll?progressState=0')
		.then(response => response.json())
		.then(data => {
			console.log("📌 서버 응답:", data);

			// 서버가 이미 progressState=0인 목록만 준다면 필터 생략 가능
			progressData = data.progressList.filter(item => item.progressState === 0);

			// 테이블 / 현재 물품 영역 업데이트 (최대 5개)
			updateTableContent(progressData.slice(0, maxItemsToShow));
		})
		.catch(error => console.error("📌 Worker 페이지 주문 목록 로딩 실패:", error));
}

/**
 * 3) 테이블 내용 + 중간(현재 물품) 영역 업데이트
 */
function updateTableContent(data) {
	const tableBody = document.getElementById("workerOrderTableBody");
	tableBody.innerHTML = "";

	// 데이터가 없으면
	if (!data || data.length === 0) {
		tableBody.innerHTML = `
            <tr>
                <td colspan="5" class="no-data-message">❌ 대기중인 물품이 없습니다.</td>
            </tr>`;
		updateCurrentSection("", "", "", false);
		return;
	}

	// 최대 5개만 행 생성
	data.forEach((item, index) => {
		// 주문 식별을 위해 <tr data-order-id="..."> 추가
		const row = document.createElement("tr");
		// orderId가 숫자라면 문자열 변환 없이 그대로 써도 됨
		row.setAttribute("data-order-id", item.orderId);

		// 필요한 만큼 <td>를 구성 (예: palletId, packagingSeq 등)
		row.innerHTML = `
            <td>${item.palletId}</td>
            <td>${item.packagingSeq}</td>
            <td>${item.orderId}</td>
            <td>${item.productName}</td>
            <td>${item.productCategory}</td>
        `;
		tableBody.appendChild(row);

		// 첫 번째 행은 '현재 물품' 섹션에 표시
		if (index === 0) {
			updateCurrentSection(
				item.orderId,
				item.productName,
				item.boxSpec,
				item.isFragile
			);
		}
	});
}

/**
 * 4) '현재 물품' 섹션 업데이트
 */
function updateCurrentSection(orderId, productName, boxSpec, isFragile) {
	// 주문번호, 물품명
	document.querySelector('.current-product-orderid').textContent = `주문 ID : ${orderId}`;
	document.querySelector('.current-product-name').textContent = `물품명 : ${productName}`;

	// 박스 규격
	const boxSizeElement = document.getElementById('currentBoxSize');
	boxSizeElement.textContent = boxSpec ? `${boxSpec}호 박스` : '-';

	// 완충재 필요 여부
	const fenderO = document.querySelector('.fender-O');
	const fenderX = document.querySelector('.fender-X');
	if (isFragile === 'Y' || isFragile === true) {
		fenderO.style.display = 'inline';
		fenderX.style.display = 'none';
	} else {
		fenderO.style.display = 'none';
		fenderX.style.display = 'inline';
	}
}

/**
 * 5) [포장완료] 버튼 클릭 시
 *    - 맨 위 물품(첫 번째 행)만 포장완료 처리
 *    - HTML: onclick="completeSelectedPackaging(this)"
 */
function completeSelectedPackaging(button) {
	// 중복 클릭 방지
	if (button.classList.contains('clicked')) return;
	button.classList.add('clicked');
	button.disabled = true;

	// 첫 번째 행 가져오기
	const firstRow = document.querySelector('#workerOrderTableBody tr:first-child');
	if (!firstRow) {
		console.log("대기중인 물품이 없습니다.");
		button.classList.remove('clicked');
		button.disabled = false;
		return;
	}

	// data-order-id 속성에서 주문번호 추출 (문자열인 경우 parseInt)
	const orderIdString = firstRow.getAttribute('data-order-id');
	const orderId = parseInt(orderIdString, 10);

	if (!confirm("현재 물품을 포장완료 처리하시겠습니까?")) {
		button.classList.remove('clicked');
		button.disabled = false;
		return;
	}

	let imageNumber = String(Math.floor(Math.random() * 100) + 1).padStart(3, '0'); // 000~100 랜덤 이미지

	// 서버가 @MessageMapping("/updateStatus")에서 Map<String,Object>로 받으므로
	// orderIds, progressState, (imageNumber) 등을 JSON 형태로 전송
	const message = {
		orderIds: [orderId],  // [숫자]
		progressState: 1,      // 1 = 포장 완료
		imageNumber: imageNumber    // 필요하다면 추가
	};
	console.log("📌 포장완료 전송 메시지:", message);

	// WebSocket(STOMP)로 메시지 전송
	if (stompClient) {
		stompClient.send("/app/updateStatus", {}, JSON.stringify(message));
	}

	// FastAPI로 이미지 검사 요청
	fetch('http://localhost:8000/api/v1/box_check/', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			order_id: parseInt(orderId),  // 문자열을 숫자로 변환
			image_number: parseInt(imageNumber)
		})
	})
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(data => {
			console.log('박스 검사 결과:', data);
		})
		.catch(error => {
			console.error('박스 검사 요청 실패:', error);
		});

	// 제거 애니메이션 (선택)
	firstRow.style.transition = 'all 0.5s';
	firstRow.style.opacity = '0';
	firstRow.style.transform = 'translateX(-100%)';

	// 0.5초 뒤 실제 제거
	setTimeout(() => {
		// progressData에서 해당 orderId 삭제
		progressData = progressData.filter(item => item.orderId !== orderId);

		// 남은 물품 중 5개만 다시 테이블 표시
		updateTableContent(progressData.slice(0, maxItemsToShow));

		// 버튼 복구
		button.classList.remove('clicked');
		button.disabled = false;
	}, 500);
}

/**
 * 6) WebSocket(STOMP) 연결
 *    - 다른 화면(Admin 등)에서 업데이트된 내용도 실시간 반영
 */
function connectWebSocket() {
	const socket = new SockJS('/ws'); // 서버쪽 registry.addEndpoint("/ws")
	stompClient = Stomp.over(socket);

	stompClient.connect({}, function(frame) {
		console.log("📌 STOMP 연결 성공:", frame);

		// /topic/updateStatus 구독 (서버 simpMessagingTemplate로 여기에 메시지 전송)
		stompClient.subscribe('/topic/updateStatus', function(message) {
			const updated = JSON.parse(message.body);
			console.log("📌 클라이언트가 받은 메시지:", updated);

			// updated.orderIds, updated.progressState 사용해 UI 갱신
			// 예) if (updated.progressState === 1) { ... 포장완료 로직 ... }
		});
	}, function(error) {
		console.error("📌 WebSocket 연결 실패:", error);
	});
}


