let currentPage = 1;
const rowsPerPage = 15;
const pageGroupSize = 10;
let totalPages = 1;
let progressData = [];

let selectedBoxSpecValue = '';
let selectedBoxStateValue = '';
let selectedProgressStateValue = '';

window.onload = () => {
    loadProgress();
    connectWebSocket();
    displayCurrentDate();
};

// 현재 날짜 표시 함수
function displayCurrentDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    
    const dateElement = document.getElementById('currentDate');
    dateElement.textContent = `${year}.${month}.${day}`;
}

function connectWebSocket() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('📌 WebSocket 연결됨:', frame);

        stompClient.subscribe('/topic/updateStatus', function(message) {
            let updatedProgress = JSON.parse(message.body);
            console.log("📌 WebSocket으로 받은 업데이트 데이터:", updatedProgress);
            updateProgressFromWebSocket(updatedProgress);
        });
		
		// 박스 상태 업데이트를 위한 새로운 구독 추가
		stompClient.subscribe('/topic/updateBoxState', function(message) {
	    	let boxData = JSON.parse(message.body);
		    console.log("📦 박스 상태 업데이트:", boxData);
		    updateBoxState(boxData);
		});

        stompClient.subscribe('/topic/progress', function(message) {
            let data = JSON.parse(message.body);
            console.log("📌 WebSocket 수신 데이터:", data);
            updateProgressFromWebSocket(data);
        });
    }, function(error) {
        console.error('📌 WebSocket 연결 실패:', error);
        setTimeout(connectWebSocket, 5000);
    });
}


function updateProgressFromWebSocket(updatedData) {
    if (!updatedData.orderIds || !Array.isArray(updatedData.orderIds)) return;

    updatedData.orderIds.forEach(orderId => {
        let row = document.querySelector(`tr[data-order-id='${orderId}']`);
        if (row) {
            let progressStateCell = row.querySelector(".progress-state");
            if (progressStateCell) {
                progressStateCell.innerText =
                    updatedData.progressState == 1 ? "포장 완료" :
                    updatedData.progressState == 2 ? "적재 완료" : "물품 대기";
            }
        }
    });
}

// 박스 상태 업데이트 함수 추가
function updateBoxState(boxData) {
    const orderId = boxData.orderId;
    const boxState = boxData.boxState;
	
	console.log("📦 박스 상태 업데이트 시도:", orderId, boxState);
    
    let row = document.querySelector(`tr[data-order-id='${orderId}']`);
    if (row) {
        let boxStateCell = row.querySelector(".box-state");  // box-state 클래스를 가진 셀 찾기
        if (boxStateCell) {
            boxStateCell.innerText = boxState === 1 ? "정상" : "파손";
            // 상태에 따른 스타일 변경
            boxStateCell.className = `box-state ${boxState === 1 ? 'text-green-600' : 'text-red-600'}`;
        }
    }
}

// 필터 이벤트 리스너 등록
document.addEventListener("DOMContentLoaded", function () {
    // document.getElementById("boxSpecFilter").addEventListener("change", applyFilters);
    // document.getElementById("boxStateFilter").addEventListener("change", applyFilters);
    // document.getElementById("progressStateFilter").addEventListener("change", applyFilters);
    document.getElementById("filterBtn").addEventListener("click", applyFilters);
    document.getElementById("prevGroup").addEventListener("click", prevPageGroup);
    document.getElementById("nextGroup").addEventListener("click", nextPageGroup);
});

//✅ 필터 적용 후 데이터 다시 로드
function applyFilters() {
    console.log("📌 필터 적용됨 ✅ 현재 페이지를 1로 초기화 후 데이터 로드");
    currentPage = 1;
    loadProgress();
}

function prevPageGroup() {
    if (currentPage > 1) {
        currentPage--;
        loadProgress();
    }
}

function nextPageGroup() {
    if (currentPage < totalPages) {
        currentPage++;
        loadProgress();
    }
}

//✅ 페이지네이션 업데이트
function updatePagination() {
    let pageNumbers = document.getElementById("pageNumbers");
    pageNumbers.innerHTML = "";

    let startPage = Math.max(1, Math.floor((currentPage - 1) / pageGroupSize) * pageGroupSize + 1);
    let endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

    for (let i = startPage; i <= endPage; i++) {
        let pageButton = document.createElement("button");
        pageButton.innerText = i;
        pageButton.classList.add("page-btn");

        if (i === currentPage) {
            pageButton.classList.add("active");
        }

        pageButton.addEventListener("click", function () {
            currentPage = i;
            loadProgress();
        });

        pageNumbers.appendChild(pageButton);
    }

    document.getElementById("prevGroup").disabled = (currentPage === 1);
    document.getElementById("nextGroup").disabled = (currentPage === totalPages);
}

//✅ 데이터 로드 함수 (필터 적용)
function loadProgress() {
    let boxSpec = document.getElementById("boxSpecFilter").value;
    let boxState = document.getElementById("boxStateFilter").value;
    let progressState = document.getElementById("progressStateFilter").value;

    let queryParams = new URLSearchParams({ 
        page: currentPage, 
        size: 15  // 20에서 15로 변경
    });

    // ✅ 필터 값이 존재할 경우에만 추가
    if (boxSpec && boxSpec !== "전체") queryParams.append("boxSpec", boxSpec);
    if (boxState && boxState !== "전체") queryParams.append("boxState", boxState);
    if (progressState && progressState !== "전체") queryParams.append("progressState", progressState);

    console.log("📌 필터링 요청 URL:", `/admin/progress/data?${queryParams.toString()}`);

    fetch(`/admin/progress/data?${queryParams.toString()}`)
    .then(response => {
        if (!response.ok) throw new Error(`HTTP 오류! 상태 코드: ${response.status}`);
        return response.json();
    })
    .then(data => {
        console.log("📌 서버 응답 데이터:", data);

        if (!data || !data.progressList) {
            console.error("🚨 서버 응답 데이터가 유효하지 않습니다.", data);
            updateProgressList([]); 
            return;
        }

        progressData = data.progressList || [];
        totalPages = data.totalPages || 1;

        console.log("📌 서버에서 받은 데이터 개수:", progressData.length);
        console.log("📌 총 페이지 수 업데이트:", totalPages);

        updateProgressList(progressData);
        updatePagination();
    })
    .catch(error => {
        console.error("🚨 데이터 로드 오류:", error);
        updateProgressList([]);
    });
}

function convertUtcToKst(utcTime) {
    if (!utcTime) return '-';

    let orderDate = new Date(Date.parse(utcTime));
    let formattedTime = orderDate.toTimeString().split(" ")[0];
    return formattedTime;
}

//✅ 테이블 업데이트
function updateProgressList(data) {
    let progressTable = document.getElementById("progressTableBody");
    progressTable.innerHTML = "";

    if (!data || data.length === 0) {
        progressTable.innerHTML = "<tr><td colspan='8' style='text-align:center;'>❌ 조회된 주문이 없습니다.</td></tr>";
        return;
    }

    data.forEach(progress => {
        let orderTime = convertUtcToKst(progress.orderTime);
        
        // ✅ boxSpec이 0이거나 NULL이면 "-"로 표시
        let boxSpecValue = (progress.boxSpec === null || progress.boxSpec === undefined || progress.boxSpec === 0) ? '-' : progress.boxSpec;

        console.log(`📦 [UI 업데이트] 주문 ID: ${progress.orderId}, boxSpec: ${boxSpecValue}`);

        let row = `<tr data-order-id="${progress.orderId}">
            <td>${progress.orderId || '-'}</td>
            <td>${progress.productName || '-'}</td>
            <td>${progress.productCategory || '-'}</td>
            <td>${boxSpecValue}</td>  <!-- ✅ 수정된 부분 -->
            <td>${progress.palletId || '-'}</td>
            <td>${progress.boxState == 0 ? '미검사' : progress.boxState == 1 ? '정상' : '파손'}</td>
            <td class="progress-state">
                ${progress.progressState == 0 ? '물품 대기' : progress.progressState == 1 ? '포장 완료' : '적재 완료'}
            </td>
        </tr>`;
        progressTable.innerHTML += row;
    });
}

// 드롭다운 관련 함수들 추가
function toggleDropdown(menuId) {
    const dropdownMenu = document.getElementById(menuId);
    const allDropdowns = document.getElementsByClassName('dropdown-menu');
    
    if (dropdownMenu) {
        const isCurrentlyOpen = dropdownMenu.classList.contains('show');
        
        // 모든 드롭다운 메뉴 닫기
        Array.from(allDropdowns).forEach(dropdown => {
            dropdown.classList.remove('show');
            const select = dropdown.previousElementSibling;
            if (select) {
                select.classList.remove('active');
            }
        });

        // 현재 클릭한 드롭다운이 닫혀있었다면 열기
        if (!isCurrentlyOpen) {
            dropdownMenu.classList.add('show');
            const dropdownSelect = dropdownMenu.previousElementSibling;
            if (dropdownSelect) {
                dropdownSelect.classList.add('active');
            }
        }
    }
}

function selectItem(element, menuId, value) {
    if (element && element.parentElement) {
        // 이전 선택 항목의 active 클래스 제거
        const prevActive = element.parentElement.querySelector('.active');
        if (prevActive) {
            prevActive.classList.remove('active');
        }
        
        // 현재 선택된 항목에 active 클래스 추가
        element.classList.add('active');

        // hidden input 업데이트
        const filterId = menuId.replace('Menu', 'Filter');
        const filterInput = document.getElementById(filterId);
        if (filterInput) {
            filterInput.value = value;
            // 필터 값 저장
            if (menuId === 'boxSpecMenu') {
                selectedBoxSpecValue = value;
            } else if (menuId === 'boxStateMenu') {
                selectedBoxStateValue = value;
            } else if (menuId === 'progressStateMenu') {
                selectedProgressStateValue = value;
            }
        }

        // 선택된 아이템 표시
        const dropdownSelect = element.parentElement.previousElementSibling;
        if (dropdownSelect) {
            // SVG 요소 보존
            const svg = dropdownSelect.querySelector('svg');
            // 텍스트 노드만 업데이트
            dropdownSelect.childNodes[0].textContent = element.textContent.trim();
            // SVG가 있으면 다시 추가
            if (svg) {
                dropdownSelect.appendChild(svg);
            }
        }

        // 드롭다운 메뉴 닫기
        toggleDropdown(menuId);

        // 필터링 즉시 적용
        applyFilters();
    }
}

// 외부 클릭 시 드롭다운 닫기
document.addEventListener('click', function(event) {
    if (!event.target.closest('.dropdown-container')) {
        const allDropdowns = document.querySelectorAll('.dropdown-menu');
        const allDropdownSelects = document.querySelectorAll('.dropdown-select');
        
        allDropdowns.forEach(dropdown => dropdown.classList.remove('show'));
        allDropdownSelects.forEach(select => select.classList.remove('active'));
    }
});


document.getElementById("downloadExcelBtn").addEventListener("click", function () {
    // ✅ URL 파라미터 추가하여 필터링된 데이터 요청
    let queryParams = new URLSearchParams();
    
    let boxSpec = document.getElementById("boxSpecFilter").value;
    let boxState = document.getElementById("boxStateFilter").value;
    let progressState = document.getElementById("progressStateFilter").value;

    // 필터 값이 있는 경우에만 파라미터 추가
    if (boxSpec) queryParams.append("boxSpec", boxSpec);
    if (boxState) queryParams.append("boxState", boxState);
    if (progressState) queryParams.append("progressState", progressState);

    // ✅ 필터링된 데이터 다운로드
    window.location.href = `/admin/progress/download/excel?${queryParams.toString()}`;
});

