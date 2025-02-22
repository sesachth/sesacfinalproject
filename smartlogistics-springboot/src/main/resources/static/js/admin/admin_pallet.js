// 페이지네이션 관련 변수
let currentPage = 1;
const rowsPerPage = 15;
const pageGroupSize = 10;
let totalPages = 1;
let palletData = [];

// 페이지 로드 시 실행
window.onload = function() {
    // 이벤트 리스너 설정
    document.getElementById('prevGroup').addEventListener('click', prevPageGroup);
    document.getElementById('nextGroup').addEventListener('click', nextPageGroup);
    
    // 검색 관련 이벤트 리스너
    document.getElementById('searchPalletBtn').addEventListener('click', function() {
        currentPage = 1;
        loadPallets();
    });
    
    document.getElementById('palletIdSearch').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            currentPage = 1;
            loadPallets();
        }
    });

    // 초기 데이터 로드
    loadPallets();
    updateSelectedFilters();
};

// 팔렛트 데이터 로드
function loadPallets() {
    const destination = document.getElementById('campFilter').value;
    const palletId = document.getElementById('palletIdSearch').value;
    
    let queryParams = new URLSearchParams();
    if (destination) queryParams.append('destination', destination);
    if (palletId) queryParams.append('palletId', palletId);

    fetch(`/admin/pallet/api/list?${queryParams.toString()}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Received data:', data); // 데이터 확인용 로그
            palletData = data || [];
            totalPages = Math.ceil(palletData.length / rowsPerPage);
            currentPage = 1;  // 검색 시 첫 페이지로 이동
            displayCurrentPageData();
            updatePagination();
            updateSelectedFilters();
        })
        .catch(error => {
            console.error('Error loading pallets:', error);
            palletData = [];
            totalPages = 1;
            currentPage = 1;
            displayCurrentPageData();
            updatePagination();
            updateSelectedFilters();
        });
}

// 현재 페이지 데이터 표시
function displayCurrentPageData() {
    const tableBody = document.getElementById("palletTableBody");
    const startIndex = (currentPage - 1) * rowsPerPage;
    const endIndex = Math.min(startIndex + rowsPerPage, palletData.length);
    
    tableBody.innerHTML = '';
    
    if (palletData.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center">등록된 팔레트가 없습니다.</td>
            </tr>`;
        return;
    }

    palletData.slice(startIndex, endIndex).forEach(pallet => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="align-center">${pallet.palletId || '-'}</td>
            <td class="align-center">${formatDecimal(pallet.load)}</td>
            <td class="align-center">${formatDecimal(pallet.width)}</td>
            <td class="align-center">${formatDecimal(pallet.depth)}</td>
            <td class="align-center">${formatDecimal(pallet.height)}</td>
            <td class="align-center">${pallet.destination || '-'}</td>
            <td class="align-center">${pallet.vehicleNumber || '-'}</td>
        `;
        tableBody.appendChild(row);
    });
}

// 페이지네이션 UI 업데이트
function updatePagination() {
    const pageNumbers = document.getElementById("pageNumbers");
    pageNumbers.innerHTML = '';

    let startPage = Math.floor((currentPage - 1) / pageGroupSize) * pageGroupSize + 1;
    let endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

    for (let i = startPage; i <= endPage; i++) {
        const button = document.createElement('button');
        button.innerText = i;
        if (i === currentPage) {
            button.classList.add('active');
        }
        button.addEventListener('click', () => {
            currentPage = i;
            displayCurrentPageData();
            updatePagination();
        });
        pageNumbers.appendChild(button);
    }

    // 이전/다음 버튼 상태 업데이트
    document.getElementById("prevGroup").disabled = currentPage === 1;
    document.getElementById("nextGroup").disabled = currentPage >= totalPages;
}

// 이전 페이지로 이동
function prevPageGroup() {
    if (currentPage > 1) {
        currentPage--;
        displayCurrentPageData();
        updatePagination();
    }
}

// 다음 페이지로 이동
function nextPageGroup() {
    if (currentPage < totalPages) {
        currentPage++;
        displayCurrentPageData();
        updatePagination();
    }
}

// 드롭다운 토글 함수
function toggleDropdown(menuId) {
    const menu = document.getElementById(menuId);
    menu.classList.toggle('show');
}

// 드롭다운 아이템 선택 함수
function selectItem(element, menuId, value) {
    // 현재 메뉴의 모든 아이템에서 active 클래스 제거
    const menuItems = element.parentElement.querySelectorAll('.dropdown-item');
    menuItems.forEach(item => item.classList.remove('active'));
    
    // 선택된 아이템에 active 클래스 추가
    element.classList.add('active');
    
    const filterId = menuId.replace('Menu', 'Filter');
    const filterInput = document.getElementById(filterId);
    if (filterInput) {
        filterInput.value = value;
    }
    
    // 드롭다운 텍스트 업데이트
    const dropdownSelect = element.parentElement.previousElementSibling;
    if (dropdownSelect) {
        const svg = dropdownSelect.querySelector('svg');
        dropdownSelect.childNodes[0].textContent = element.textContent.trim();
        if (svg) {
            dropdownSelect.appendChild(svg);
        }
    }
    
    // 드롭다운 메뉴 닫기
    toggleDropdown(menuId);
    
    // 필터 적용하여 데이터 즉시 로드
    currentPage = 1;
    loadPallets();
}

// 현재 선택된 필터 표시
function updateSelectedFilters() {
    const destination = document.getElementById('campFilter').value;
    
    // Destination 필터 업데이트
    const campItems = document.querySelectorAll('#campMenu .dropdown-item');
    campItems.forEach(item => {
        if (item.getAttribute('data-value') === destination) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });
}

// 드롭다운 외부 클릭 시 닫기
document.addEventListener('click', function(event) {
    const dropdowns = document.querySelectorAll('.dropdown-menu');
    dropdowns.forEach(dropdown => {
        if (!event.target.closest('.dropdown-container')) {
            dropdown.classList.remove('show');
        }
    });
});

// 숫자 포맷팅 함수 수정
function formatDecimal(value) {
    return value ? Number(value).toFixed(1) : '-';
}


// 엑셀 다운로드 버튼 이벤트 리스너 
document.getElementById('downloadExcelBtn').addEventListener('click', function() {
const destination = document.getElementById('campFilter').value;
const palletId = document.getElementById('palletIdSearch').value;
let queryParams = new URLSearchParams();
if (destination) queryParams.append('destination', destination);
if (palletId) queryParams.append('palletId', palletId);
// vehicleNumber 등 다른 필터 조건이 있다면 추가

 // ✅ 필터링된 데이터 다운로드
    window.location.href = `/admin/pallet/download/excel?${queryParams.toString()}`;
});