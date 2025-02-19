// 선택된 값을 저장할 변수
let selectedCampValue = '';

document.addEventListener('DOMContentLoaded', function() {
    const dateFilter = document.getElementById('dateFilter');
    const dropdowns = document.getElementsByClassName('dropdown-menu');
    let isCalendarOpen = false;

    // ✅ 오늘 날짜를 기본값으로 설정
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    const formattedDate = `${year}-${month}-${day}`;
    
    // dateFilter input에 오늘 날짜 설정
    dateFilter.value = formattedDate;
    
    // 날짜 표시 텍스트 업데이트
    const formattedDisplayDate = today.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
    document.querySelector('.date-placeholder').textContent = formattedDisplayDate;

    // 달력 클릭 이벤트
    dateFilter.addEventListener('click', function(e) {
        // 모든 드롭다운 메뉴 닫기
        Array.from(dropdowns).forEach(dropdown => {
            dropdown.classList.remove('show');
            const dropdownSelect = dropdown.previousElementSibling;
            if (dropdownSelect) {
                dropdownSelect.classList.remove('active');
            }
        });

        const calendarContainer = this.closest('.dropdown-container');
        const dropdownSelect = calendarContainer.querySelector('.dropdown-select');

        // 달력이 열려있으면 닫고, 닫혀있으면 열기
        if (isCalendarOpen) {
            this.blur();  // 달력 닫기
            isCalendarOpen = false;
            e.preventDefault();  // 기본 동작 막기
            // active 상태 제거
            if (dropdownSelect) {
                dropdownSelect.classList.remove('active');
            }
        } else {
            this.focus();  // 달력 열기
            isCalendarOpen = true;
            // active 상태 추가
            if (dropdownSelect) {
                dropdownSelect.classList.add('active');
            }
        }
    });

    // 날짜 선택 이벤트
    dateFilter.addEventListener('change', function(e) {
        const date = new Date(this.value);
        const formattedDate = date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
        document.querySelector('.date-placeholder').textContent = formattedDate;
        this.blur();
        isCalendarOpen = false;

        // 날짜 선택 후 active 상태 제거
        const calendarContainer = this.closest('.dropdown-container');
        const dropdownSelect = calendarContainer.querySelector('.dropdown-select');
        if (dropdownSelect) {
            dropdownSelect.classList.remove('active');  // active 클래스 제거
        }
    });

    // 외부 클릭 시 달력 닫기
    document.addEventListener('click', function(event) {
        if (!event.target.closest('.dropdown-container')) {
            dateFilter.blur();
            isCalendarOpen = false;
            // active 상태 제거
            const allDropdownSelects = document.querySelectorAll('.dropdown-select');
            allDropdownSelects.forEach(select => {
                select.classList.remove('active');
            });
        }
    });

    /*
    // 초기값 설정이 필요한 경우 여기에 추가
    const defaultItem = document.querySelector('.dropdown-item[data-value=""]');
    if (defaultItem) {
        selectItem(defaultItem, 'campMenu', '');
    }
    */
});

// 드롭다운 아이템 선택 함수
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
        const campFilter = document.getElementById('campFilter');
        if (campFilter) {
            campFilter.value = value;
        }

        // 선택된 아이템 표시 
        const dropdownSelect = element.parentElement.previousElementSibling;
        if (dropdownSelect) {
            // SVG 요소 보존
            const svg = dropdownSelect.querySelector('svg');
            
            // 텍스트 내용 업데이트
            dropdownSelect.textContent = element.textContent;
            
            // SVG 다시 추가
            if (svg) {
                dropdownSelect.appendChild(svg);
            }
        }

        // 드롭다운 메뉴 닫기
        toggleDropdown(menuId);
    }
}

// 드롭다운 토글 함수
function toggleDropdown(menuId) {
    const dropdownMenu = document.getElementById(menuId);
    if (dropdownMenu) {
        // 달력 닫기
        const dateFilter = document.getElementById('dateFilter');
        if (dateFilter) {
            dateFilter.blur();
        }

        // 다른 드롭다운 메뉴 닫기
        const allDropdowns = document.getElementsByClassName('dropdown-menu');
        Array.from(allDropdowns).forEach(function(dropdown) {
            if (dropdown.id !== menuId) {
                dropdown.classList.remove('show');
                const otherSelect = dropdown.previousElementSibling;
                if (otherSelect) {
                    otherSelect.classList.remove('active');
                }
            }
        });

        // 클릭한 드롭다운 토글
        dropdownMenu.classList.toggle('show');
        const dropdownSelect = dropdownMenu.previousElementSibling;
        if (dropdownSelect) {
            dropdownSelect.classList.toggle('active');
        }
    }
}


// 주문생성 관련 JS
let currentPage = 1;
const rowsPerPage = 20;
const pageGroupSize = 10;
let totalPages = 1;
let orders = [];

window.onload = () => loadOrders();

document.getElementById("filterBtn").addEventListener("click", function () {
    currentPage = 1;
    loadOrders();
});

document.getElementById("generateOrderBtn").addEventListener("click", function () {
    showLoading();  // ✅ 로딩 화면 표시
    fetch("/admin/order/generate", { method: "POST" })
        .then(response => response.json())
        .then(data => {
            hideLoading();  // ✅ 주문 생성 완료 후 로딩 숨김
            alert(data.message);
            setTimeout(() => location.reload(), 1000);  // ✅ 주문 생성 후 자동 새로고침
        })
        .catch(error => {
            hideLoading();  // ✅ 오류 발생 시 로딩 숨김
            console.error("Error:", error);
        });
});


document.getElementById("searchOrderBtn").addEventListener("click", function () {
    searchOrderByNum();
});

document.getElementById("orderNumSearch").addEventListener("keypress", function (event) {
    if (event.key === "Enter") {
        searchOrderByNum();
    }
});

function searchOrderByNum() {
    let orderNum = document.getElementById("orderNumSearch").value.trim();

    if (orderNum === "") {
        loadOrders();  // 주문번호 입력이 비어 있으면 전체 주문을 다시 불러오기
        return;
    }

    fetch(`/admin/order/api/search?orderNum=${orderNum}`)
        .then(response => {
            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error("❌ 해당 주문번호로 조회된 주문이 없습니다.");
                }
                throw new Error("❌ 서버 오류 발생");
            }
            return response.json();
        })
        .then(data => {
            if (!data.orders || data.orders.length === 0) {
                alert("❌ 해당 주문번호로 조회된 주문이 없습니다.");
                document.getElementById("orderTableBody").innerHTML =
                    "<tr><td colspan='6' style='text-align:center;'>❌ 주문이 없습니다.</td></tr>";
            } else {
                orders = data.orders;  // ✅ 주문 데이터 업데이트
                updateOrderList(orders);  // ✅ 검색된 주문 리스트를 UI에 반영
            }
        })
        .catch(error => {
            alert(error.message); // ❌ 에러 메시지를 사용자에게 알림
            document.getElementById("orderTableBody").innerHTML =
                "<tr><td colspan='6' style='text-align:center;'>❌ 주문이 없습니다.</td></tr>";
        });
}


document.getElementById("downloadExcelBtn").addEventListener("click", function () {
    let date = document.getElementById("dateFilter").value || new Date().toISOString().split('T')[0];
    let camp = document.getElementById("campFilter").value || "";
    let orderNum = document.getElementById("orderNumSearch").value.trim(); 

    // ✅ URL 파라미터 추가하여 필터링된 데이터 요청
    let queryParams = new URLSearchParams();
    queryParams.append("date", date);
    if (camp) queryParams.append("destination", camp);
    if (orderNum) queryParams.append("orderNum", orderNum);

    // ✅ 필터링된 데이터 다운로드
    window.location.href = `/admin/order/download/excel?${queryParams.toString()}`;
});


document.getElementById("prevGroup").addEventListener("click", function () {
    if (currentPage > 1) {
        currentPage--;
        loadOrders();
    }
});

document.getElementById("nextGroup").addEventListener("click", function () {
    if (currentPage < totalPages) {
        currentPage++;
        loadOrders();
    }
});

function loadOrders(page = currentPage) {
    let date = document.getElementById("dateFilter").value || new Date().toISOString().split('T')[0];
    let camp = document.getElementById("campFilter").value;

    let queryParams = new URLSearchParams({ page, size: 20 });
    queryParams.append("date", date); // date 파라미터를 항상 포함
    if (camp) queryParams.append("destination", camp);

    fetch(`/admin/order/api?${queryParams.toString()}`)
        .then(response => response.json())
        .then(data => {
            orders = data.orders;
            totalPages = data.totalPages || 1;  // ✅ `totalPages` 값이 1일 때도 기본 설정

            updateOrderList();
            updatePagination();  // ✅ 페이지네이션 강제 실행
        })
        .catch(error => {
            console.error("Error:", error);
            document.getElementById("orderTableBody").innerHTML =
                "<tr><td colspan='8' style='text-align:center;'>❌ 주문이 없습니다.</td></tr>";
        });
}


function updateOrderList(orderData = orders) {
    let orderTable = document.getElementById("orderTableBody");
    orderTable.innerHTML = "";

    if (!orderData || orderData.length === 0) {
        orderTable.innerHTML = "<tr><td colspan='6' style='text-align:center;'>❌ 주문이 없습니다.</td></tr>";
        return;
    }

    orderData.forEach(order => {
        let formattedDate = order.orderTime.split("T")[0]; // YYYY-MM-DD
        let formattedTime = order.orderTime.split("T")[1].split(".")[0]; // HH:MM:SS

        let row = `<tr>
            <td>${order.orderId || '-'}</td>
            <td>${order.orderNum || '-'}</td>
            <td>${formattedDate || '-'}</td>
            <td>${formattedTime || '-'}</td>
            <td>${order.destination || '-'}</td>
            <td>${order.productId || '-'}</td>
        </tr>`;
        orderTable.innerHTML += row;
    });

}

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
            loadOrders(i);
        });

        pageNumbers.appendChild(pageButton);
    }

    document.getElementById("prevGroup").disabled = (currentPage === 1);
    document.getElementById("nextGroup").disabled = (currentPage === totalPages);
}

// ✅ 로딩 화면 표시 함수
function showLoading() {
    document.getElementById("loadingOverlay").style.display = "flex";
}

// ✅ 로딩 화면 숨김 함수
function hideLoading() {
    document.getElementById("loadingOverlay").style.display = "none";
}