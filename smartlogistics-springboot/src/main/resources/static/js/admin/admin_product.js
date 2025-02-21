// 페이지네이션 관련 변수
let currentPage = 1;
const rowsPerPage = 15;
const pageGroupSize = 10;
let totalPages = 1;
let productData = [];

// 페이지 로드 시 실행
window.onload = () => {
    loadProducts();
    updateSelectedFilters();
    
    // 이전/다음 버튼 이벤트 리스너 추가
    document.getElementById("prevGroup").addEventListener("click", prevPageGroup);
    document.getElementById("nextGroup").addEventListener("click", nextPageGroup);
};

// 검색 관련 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', function() {
    // 검색 버튼 클릭 이벤트
    document.getElementById("searchProductBtn").addEventListener("click", function(e) {
        e.preventDefault();
        currentPage = 1;
        loadProducts();
    });

    // 검색어 입력 필드 Enter 키 이벤트
    document.getElementById("productNameSearch").addEventListener("keypress", function(e) {
        if (e.key === "Enter") {
            e.preventDefault();
            currentPage = 1;
            loadProducts();
        }
    });
});

// loadProducts 함수
function loadProducts() {
    let searchQuery = document.getElementById("productNameSearch").value.trim();
    let category = document.getElementById("categoryFilter").value || '전체';
    let fragile = document.getElementById("fragileFilter").value || '전체';

    let queryParams = new URLSearchParams();
    queryParams.append("filter", category);
    queryParams.append("isFragile", fragile);
    if (searchQuery) queryParams.append("name", searchQuery);

    console.log("검색 요청 URL:", `/admin/product/search?${queryParams.toString()}`);
    console.log("필터 상태 - Category:", category, "Fragile:", fragile);

    fetch(`/admin/product/search?${queryParams.toString()}`)
        .then(response => {
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            return response.json();
        })
        .then(data => {
            console.log("검색 결과:", data);
            productData = data || [];
            totalPages = Math.ceil(productData.length / rowsPerPage);
            displayCurrentPageData();
            updatePagination();
            updateSelectedFilters();
        })
        .catch(error => {
            console.error("검색 중 오류 발생:", error);
            productData = [];
            totalPages = 1;
            displayCurrentPageData();
            updatePagination();
            updateSelectedFilters();
        });
}

// 현재 페이지 데이터 표시
function displayCurrentPageData() {
    const startIndex = (currentPage - 1) * rowsPerPage;
    const endIndex = Math.min(startIndex + rowsPerPage, productData.length);
    const currentPageData = productData.slice(startIndex, endIndex);
    
    updateProductTable(currentPageData);
}

// 소수점 첫째자리까지 표시하는 함수
function formatDecimal(number) {
    return number ? Number(number).toFixed(1) : '-';
}

// 테이블 업데이트 함수
function updateProductTable(products) {
    const tableBody = document.getElementById("productTableBody");
    tableBody.innerHTML = '';

    if (!products || products.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="9" class="text-center">No products found</td>
            </tr>`;
        return;
    }

    products.forEach(product => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="align-center">${product.productId || '-'}</td>
            <td class="align-left">${product.name || '-'}</td>
            <td class="align-center">${formatDecimal(product.width)}</td>
            <td class="align-center">${formatDecimal(product.depth)}</td>
            <td class="align-center">${formatDecimal(product.height)}</td>
            <td class="align-center">${formatDecimal(product.weight)}</td>
            <td class="align-left">${product.category || '-'}</td>
            <td class="align-center">
                ${product.fragile ? `
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="10" cy="10" r="9" fill="#FF3B30"/>
                        <path d="M10 5V11" stroke="white" stroke-width="2" stroke-linecap="round"/>
                        <circle cx="10" cy="14.5" r="1" fill="white"/>
                    </svg>
                ` : ''}
            </td>
            <td class="align-center">
                <div class="btn-reveal-trigger position-static">
                    <button class="btn btn-sm dropdown-toggle dropdown-caret-none btn-reveal" type="button" data-bs-toggle="dropdown">
                        •••
                    </button>
                    <div class="dropdown-menu py-2">
                        <button class="dropdown-item" onclick="handleEdit(${product.productId})">Edit</button>
                        <button class="dropdown-item" onclick="handleDelete(${product.productId})">Delete</button>
                    </div>
                </div>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

// 페이지네이션 업데이트 함수
function updatePagination() {
    const pageNumbers = document.getElementById("pageNumbers");
    pageNumbers.innerHTML = '';

    let startPage = Math.floor((currentPage - 1) / pageGroupSize) * pageGroupSize + 1;
    let endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

    for (let i = startPage; i <= endPage; i++) {
        const button = document.createElement('button');
        button.innerText = i;
        button.classList.add('page-button');
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

    document.getElementById("prevGroup").disabled = currentPage === 1;
    document.getElementById("nextGroup").disabled = currentPage === totalPages;
}

// 이전 페이지 그룹으로 이동
function prevPageGroup() {
    if (currentPage > 1) {
        currentPage--;
        displayCurrentPageData();
        updatePagination();
    }
}

// 다음 페이지 그룹으로 이동
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
    const allMenus = document.querySelectorAll('.toggle-menu');
    
    // 다른 모든 드롭다운 메뉴를 닫음
    allMenus.forEach(m => {
        if (m.id !== menuId) {
            m.classList.remove('show');
        }
    });
    
    // 현재 선택한 메뉴의 토글
    menu.classList.toggle('show');
}

// 드롭다운 아이템 선택 함수
function selectItem(element, menuId, value) {
    // 현재 메뉴의 모든 아이템에서 active 클래스 제거
    const menuItems = element.parentElement.querySelectorAll('.toggle-item');
    menuItems.forEach(item => item.classList.remove('active'));
    
    // 선택된 아이템에 active 클래스 추가
    element.classList.add('active');
    
    // 현재 선택한 필터 값 설정
    const filterId = menuId.replace('Menu', 'Filter');
    const filterInput = document.getElementById(filterId);
    if (filterInput) {
        filterInput.value = value || '전체';
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
    loadProducts();
}

// 드롭다운 외부 클릭 시 닫기
document.addEventListener('click', function(event) {
    const dropdowns = document.querySelectorAll('.toggle-menu');
    
    // 클릭된 요소가 토글 버튼이나 메뉴가 아닌 경우
    if (!event.target.closest('.toggle-select') && !event.target.closest('.toggle-menu')) {
        dropdowns.forEach(dropdown => {
            dropdown.classList.remove('show');
        });
    }
});

// Excel 다운로드
document.getElementById("downloadExcelBtn").addEventListener("click", function () {
    let category = document.getElementById("categoryFilter").value || '전체';
    let fragile = document.getElementById("fragileFilter").value || '전체';

    let queryParams = new URLSearchParams();
    queryParams.append("filter", category);
    queryParams.append("isFragile", fragile);

    window.location.href = `/admin/product/excel?${queryParams.toString()}`;
});

// 페이지 로드 시 현재 선택된 필터 표시
function updateSelectedFilters() {
    const categoryValue = document.getElementById('categoryFilter').value;
    const fragileValue = document.getElementById('fragileFilter').value;
    
    // Category 필터 업데이트
    const categoryItems = document.querySelectorAll('#categoryMenu .toggle-item');
    categoryItems.forEach(item => {
        if (item.getAttribute('data-value') === categoryValue) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });
    
    // Fragile 필터 업데이트
    const fragileItems = document.querySelectorAll('#fragileMenu .toggle-item');
    fragileItems.forEach(item => {
        if (item.getAttribute('data-value') === fragileValue) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });
}

// Add Product 버튼 이벤트
document.getElementById("addBtn").addEventListener("click", function() {
    clearModalInputs();
    $("#exampleModalLabel").text("Add Product");
    $("#exampleModal").modal('show');
});

// Submit 버튼 이벤트
document.getElementById("submit-btn").addEventListener("click", handleSubmit);

// Edit 처리
function handleEdit(productId) {
    const product = productData.find(p => p.productId === productId);
    if (!product) return;

    $("#exampleModalLabel").text("Edit Product");
    $("#modalIndex").val(product.productId);
    $("#modalName").val(product.name);
    $("#modalWidth").val(product.width);
    $("#modalDepth").val(product.depth);
    $("#modalHeight").val(product.height);
    $("#modalWeight").val(product.weight);
    $("#modalCategory").val(product.category);
    $("#fragileCheckbox").prop("checked", product.fragile);
    
    $("#exampleModal").modal('show');
}

// Delete 처리
function handleDelete(productId) {
    if (!confirm("정말로 이 제품을 삭제하시겠습니까?")) return;

    fetch(`/admin/product/delete/${productId}`, {
        method: 'GET'
    })
    .then(response => {
        if (!response.ok) throw new Error('Delete failed');
        loadProducts();
    })
    .catch(error => {
        console.error('Delete error:', error);
        alert("제품 삭제 중 오류가 발생했습니다.");
    });
}

// Submit 처리
function handleSubmit() {
    const product = {
        productId: $("#modalIndex").val().trim(),
        name: $("#modalName").val().trim(),
        width: $("#modalWidth").val().trim(),
        depth: $("#modalDepth").val().trim(),
        height: $("#modalHeight").val().trim(),
        weight: $("#modalWeight").val().trim(),
        category: $("#modalCategory").val().trim(),
        fragile: $("#fragileCheckbox").is(":checked")
    };

    if (!validateProduct(product)) {
        alert("모든 필드를 입력해주세요.");
        return;
    }

    $.ajax({
        url: product.productId ? "/admin/product/update" : "/admin/product/add",
        type: "POST",
        data: product,
        success: function() {
            $("#exampleModal").modal("hide");
            loadProducts();
        },
        error: function() {
            console.error("저장 오류:");
            alert("제품 저장 중 오류가 발생했습니다.");
        }
    });
}

// 입력값 검증
function validateProduct(product) {
    return product.name && 
           product.width && 
           product.depth && 
           product.height && 
           product.weight && 
           product.category;
}

// 모달 입력 필드 초기화
function clearModalInputs() {
    $("#modalIndex").val("");
    $("#modalName").val("");
    $("#modalWidth").val("");
    $("#modalDepth").val("");
    $("#modalHeight").val("");
    $("#modalWeight").val("");
    $("#modalCategory").val("");
    $("#fragileCheckbox").prop("checked", false);
}
