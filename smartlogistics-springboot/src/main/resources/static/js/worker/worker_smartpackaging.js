//✅ 페이지네이션 변수
let currentPage = 1;
const maxVisiblePages = 7; // 한 번에 보이는 페이지 수
let totalPages = 1;

// ✅ 주문 목록 가져오기
function loadWorkerOrders() {
    fetch('/admin/progress/data')
        .then(response => response.json())
        .then(data => {
            console.log("📌 Worker 페이지 주문 목록 응답:", data);

            let tableBody = document.getElementById("workerOrderTableBody");
            tableBody.innerHTML = "";

            if (!data.progressList || data.progressList.length === 0) {
                tableBody.innerHTML = "<tr><td colspan='7' class='p-4 text-center text-gray-500'>❌ 데이터가 없습니다.</td></tr>";
                return;
            }

            data.progressList.forEach(order => {
                let orderTime = convertUtcToKst(order.orderTime);
                let row = `<tr class="hover:bg-gray-100 transition">
                    <td class="p-3 border border-gray-300">
                        <input type="checkbox" class="order-checkbox" data-id="${order.orderId}" />
                    </td>
                    <td class="p-3 border border-gray-300">${order.orderId}</td>
                    <td class="p-3 border border-gray-300">${order.productName}</td>
                    <td class="p-3 border border-gray-300">${order.productCategory}</td>
                    <td class="p-3 border border-gray-300">${orderTime}</td>
                    <td class="p-3 border border-gray-300">
                        ${order.boxState === 0 ? '미검사' : order.boxState === 1 ? '정상' : '파손'}
                    </td>
                    <td class="p-3 border border-gray-300 progress-state">
                        ${order.progressState === 0 ? '물품 대기' : order.progressState === 1 ? '포장 완료' : '적재 완료'}
                    </td>
                </tr>`;
                tableBody.innerHTML += row;
            });

            // 전체 선택 체크박스 이벤트 등록
            document.getElementById("selectAllCheckbox").addEventListener("change", toggleSelectAll);
            document.querySelectorAll(".order-checkbox").forEach(checkbox => {
                checkbox.addEventListener("change", updateSelectAllCheckbox);
            });
        })
        .catch(error => console.error("📌 Worker 페이지 주문 목록 로딩 실패:", error));
}

// ✅ 포장 완료 버튼 클릭 시 선택된 주문 상태 변경
function completeSelectedPackaging() {
    let selectedOrders = [];
    document.querySelectorAll(".order-checkbox:checked").forEach(checkbox => {
        selectedOrders.push(checkbox.dataset.id);
    });

    if (selectedOrders.length === 0) {
        alert("✅ 포장할 주문을 선택하세요.");
        return;
    }

    let message = {
        orderIds: selectedOrders,
        progressState: 1 // 포장 완료
    };

    console.log("📌 WebSocket 전송 메시지:", message);

    stompClient.send("/app/updateStatus", {}, JSON.stringify(message));

    alert("✅ 선택한 주문이 포장 완료되었습니다.");

    selectedOrders.forEach(orderId => {
        let row = document.querySelector(`tr[data-order-id='${orderId}']`);
        if (row) {
            row.querySelector(".progress-state").innerText = "포장 완료";
        }
    });
}

// ✅ WebSocket 설정
function connectWebSocket() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('📌 WebSocket 연결됨:', frame);
        stompClient.subscribe('/topic/progress', function(message) {
            let data = JSON.parse(message.body);
            updateProgressState(data.orderIds, data.progressState);
        });
    });
}

// ✅ 특정 주문만 상태 업데이트
function updateProgressState(orderIds, progressState) {
    orderIds.forEach(orderId => {
        let row = document.querySelector(`tr[data-order-id='${orderId}']`);
        if (row) {
            row.querySelector(".progress-state").innerText =
                progressState === 1 ? '포장 완료' :
                progressState === 2 ? '적재 완료' : '물품 대기';
        }
    });
}

 // ✅ 전체 선택 체크박스 기능 (수정됨)
function toggleSelectAll() {
    let isChecked = document.getElementById("selectAllCheckbox").checked;
    document.querySelectorAll(".order-checkbox").forEach(checkbox => {
        checkbox.checked = isChecked;
    });
}

 // ✅ 개별 체크박스 해제 시 전체 선택 체크박스 상태 업데이트 (수정됨)
function updateSelectAllCheckbox() {
    let total = document.querySelectorAll(".order-checkbox").length;
    let checked = document.querySelectorAll(".order-checkbox:checked").length;
    document.getElementById("selectAllCheckbox").checked = total === checked;
}

// ✅ UTC → KST 변환 함수
function convertUtcToKst(utcTime) {
    if (!utcTime) return '-';
    let orderDate = new Date(Date.parse(utcTime));
    orderDate.setHours(orderDate.getHours());
    return orderDate.toTimeString().split(" ")[0];
}

// ✅ 페이지 로드 시 자동 실행
document.addEventListener("DOMContentLoaded", function () {
    console.log("📌 Worker 페이지 DOM 로드 완료, 주문 목록 로드 시작");
    loadWorkerOrders();
    connectWebSocket();
});

// ✅ 페이지네이션 업데이트
    function updatePagination() {
        const paginationContainer = document.getElementById("pageNumbers");
        paginationContainer.innerHTML = "";
    
        let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
        let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
    
        if (endPage - startPage + 1 < maxVisiblePages) {
            startPage = Math.max(1, endPage - maxVisiblePages + 1);
        }
    
        // 이전 버튼 활성화 여부 설정
        document.getElementById("prevGroup").disabled = currentPage === 1;
        document.getElementById("nextGroup").disabled = currentPage === totalPages;
    
        for (let i = startPage; i <= endPage; i++) {
            let pageButton = document.createElement("button");
            pageButton.innerText = i;
            pageButton.classList.add("px-4", "py-2", "rounded-lg", "transition");
    
            if (i === currentPage) {
                pageButton.classList.add("bg-blue-500", "text-white");
            } else {
                pageButton.classList.add("bg-gray-200", "text-gray-700", "hover:bg-gray-300");
            }
    
            pageButton.addEventListener("click", function () {
                if (currentPage !== i) {
                    currentPage = i;
                    loadWorkerOrders(currentPage);
                }
            });
    
            paginationContainer.appendChild(pageButton);
        }
    }

    // ✅ 주문 목록 가져오기 (페이지네이션 적용)
    function loadWorkerOrders(page = 1) {
        fetch(`/admin/progress/data?page=${page}`)
            .then(response => response.json())
            .then(data => {
                console.log("📌 Worker 페이지 주문 목록 응답:", data);

                let tableBody = document.getElementById("workerOrderTableBody");
                tableBody.innerHTML = "";

                if (!data.progressList || data.progressList.length === 0) {
                    tableBody.innerHTML = "<tr><td colspan='7' class='p-4 text-center text-gray-500'>❌ 데이터가 없습니다.</td></tr>";
                    return;
                }

                data.progressList.forEach(order => {
                    let orderTime = convertUtcToKst(order.orderTime);
                    let row = `<tr class="hover:bg-gray-100 transition">
                        <td class="p-3 border border-gray-300">
                            <input type="checkbox" class="order-checkbox cursor-pointer" data-id="${order.orderId}" />
                        </td>
                        <td class="p-3 border border-gray-300">${order.orderId}</td>
                        <td class="p-3 border border-gray-300">${order.productName}</td>
                        <td class="p-3 border border-gray-300">${order.productCategory}</td>
                        <td class="p-3 border border-gray-300">${orderTime}</td>
                        <td class="p-3 border border-gray-300">
                            ${order.boxState === 0 ? '미검사' : order.boxState === 1 ? '정상' : '파손'}
                        </td>
                        <td class="p-3 border border-gray-300 progress-state">
                            ${order.progressState === 0 ? '물품 대기' : order.progressState === 1 ? '포장 완료' : '적재 완료'}
                        </td>
                    </tr>`;
                    tableBody.innerHTML += row;
                });

                // ✅ 이벤트 리스너 재등록 (수정됨)
                document.getElementById("selectAllCheckbox").addEventListener("change", toggleSelectAll);
                document.querySelectorAll(".order-checkbox").forEach(checkbox => {
                    checkbox.addEventListener("change", updateSelectAllCheckbox);
                });

                totalPages = data.totalPages || 1;
                updatePagination();
            })
            .catch(error => console.error("📌 Worker 페이지 주문 목록 로딩 실패:", error));
    }

    // ✅ 페이지 로드 시 자동 실행
    document.addEventListener("DOMContentLoaded", function () {
        console.log("📌 Worker 페이지 DOM 로드 완료, 주문 목록 로드 시작");
        loadWorkerOrders();
    });


    function completeSelectedPackaging(button) {
        // 이미 클릭된 상태면 함수 종료
        if (button.classList.contains('clicked')) {
            return;
        }
        
        // 버튼에 clicked 클래스 추가
        button.classList.add('clicked');
        
        // 버튼 비활성화
        button.disabled = true;
        
        // 여기에 포장완료 관련 로직 추가
        console.log('포장완료 버튼이 클릭되었습니다.');
        
        // 선택적: 3초 후 버튼 상태 복구
        /*
        setTimeout(() => {
            button.classList.remove('clicked');
            button.disabled = false;
        }, 3000);
        */
    }

