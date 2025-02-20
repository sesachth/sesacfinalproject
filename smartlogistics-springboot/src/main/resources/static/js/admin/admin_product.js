    // 페이지네이션 관련 전역 변수
    let currentPage = 1;
    let rowsPerPage = 20;
    let pageGroupSize = 10;
    let totalPages = 1;
    let currentProducts = []; // 전체 제품 데이터 저장용

    // 페이지 로드 시 실행
    $(document).ready(function() {
        initializeEventHandlers();
        initializePaginationButtons(); // 페이지네이션 버튼 초기화 추가
        searchProducts();
    });

// 모든 이벤트 핸들러 초기화
function initializeEventHandlers() {
    // Add Product 버튼 이벤트
    $("#addBtn").on("click", function() {
        clearModalInputs();
        $("#exampleModalLabel").text("Add Product");
        $("#exampleModal").modal('show');
    });

    // Submit 버튼 이벤트
    $("#submit-btn").on("click", handleSubmit);

    // 검색 폼 제출 이벤트
    $(".search-form").on("submit", function(e) {
        e.preventDefault();
        searchProducts();
    });

    // 필터 변경 이벤트
    $("#categoryFilter, #fragileFilter").on("change", function() {
    searchProducts();
    });
}

// 통합 검색 함수
function searchProducts() {
    let searchQuery = $(".search-form input[name='name']").val().trim();
    let filterInfo = {
        name: searchQuery,
        filter: $("#categoryFilter").val()?.trim() || "전체",
        isFragile: $("#fragileFilter").val()?.trim() || "전체"
    };

    $.ajax({
        url: "/admin/product/search",
        type: "GET",
        data: filterInfo,
        success: function(data) {
            updateProductList(data);
        },
        error: function(xhr, status, error) {
            console.error("검색 오류:", error);
            alert("검색 중 오류가 발생했습니다.");
        }
    });
}

// 제품 목록 업데이트
function updateProductList(products) {
    currentProducts = products;
    // 최소 1페이지는 항상 존재하도록 설정
    totalPages = Math.max(1, Math.ceil(products.length / rowsPerPage));
    currentPage = 1; // 검색/필터링 시 첫 페이지로 초기화
    displayCurrentPage();
    updatePagination();
}

// 현재 페이지 표시
function displayCurrentPage() {
    let startIndex = (currentPage - 1) * rowsPerPage;
    let endIndex = Math.min(startIndex + rowsPerPage, currentProducts.length);
    let currentPageProducts = currentProducts.slice(startIndex, endIndex);

    let html = '';
    if (currentPageProducts.length === 0) {
        html = '<tr><td colspan="9" class="text-center">검색 결과가 없습니다.</td></tr>';
    }
    else {
        currentPageProducts.forEach((product, index) => {
            html += `<tr >
                <td class="productId d-none">${product.productId}</td>
                <td class="index">${startIndex + index + 1}</td>
                <td class="name">${product.name}</td>
                <td class="width">${product.width}</td>
                <td class="depth">${product.depth}</td>
                <td class="height">${product.height}</td>
                <td class="weight">${product.weight}</td>
                <td class="category">${product.category}</td>
                <td class="isFragile">${product.isFragile ? 'Yes' : 'No'}</td>
                <td>
                    <div class="btn-reveal-trigger position-static">
                        <button class="btn btn-sm dropdown-toggle dropdown-caret-none transition-none btn-reveal" type="button" data-bs-toggle="dropdown" data-boundary="window" aria-haspopup="true" aria-expanded="true" data-bs-reference="parent">
                            •••
                        </button>
                        <div class="dropdown-menu py-2">
                            <a class="dropdown-item editBtn" onclick="handleEdit(this)">Edit</a>
                            <a class="dropdown-item deleteBtn">Delete</a>
                        </div>
                    </div>
                </td>
            </tr>`;
        });
    }

    $("#listBody").html(html);
    bindDeleteButtons();
}

// 페이지네이션 UI 업데이트
function updatePagination() {
    let pageNumbersElement = $("#pageNumbers");
    pageNumbersElement.empty();

    // 검색 결과가 없을 때도 페이지네이션 표시
    if (currentProducts.length === 0) {
        $("#prevGroup, #nextGroup").hide();
        let btn = $("<button>")
            .text(1)
            .addClass("btn mx-1 btn-purple btn-purple-active")
            .prop("disabled", true);
        pageNumbersElement.append(btn);
        return;
    }

    $("#prevGroup, #nextGroup").show();

    let startPage = Math.floor((currentPage - 1) / pageGroupSize) * pageGroupSize + 1;
    let endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

    // 최소 1페이지는 항상 표시
    endPage = Math.max(1, endPage);

    // 이전 그룹 버튼
    $("#prevGroup")
        .prop("disabled", startPage <= 1)
        .removeClass("btn-secondary")
        .addClass("btn-purple");

    // 페이지 번호 버튼들
    for (let i = startPage; i <= endPage; i++) {
        let btn = $("<button>")
            .text(i)
            .addClass("btn mx-1 btn-purple")
            .on("click", function() {
                if (currentPage !== i) {
                    currentPage = i;
                    displayCurrentPage();
                    updatePagination();
                }
            });
    
        if (i === currentPage) {
            btn.removeClass("btn-purple").addClass("btn-purple-active");
        }
    
        pageNumbersElement.append(btn);
    }

    // 다음 그룹 버튼
    $("#nextGroup")
        .prop("disabled", endPage >= totalPages)
        .removeClass("btn-secondary")
        .addClass("btn-purple");
}

// 이전/다음 그룹 버튼 이벤트 핸들러
function initializePaginationButtons() {
$("#prevGroup").on("click", function() {
    if (!$(this).prop("disabled")) {
        currentPage = Math.max(1, currentPage - pageGroupSize);
        displayCurrentPage();
        updatePagination();
    }
});

$("#nextGroup").on("click", function() {
    if (!$(this).prop("disabled")) {
        currentPage = Math.min(totalPages, currentPage + pageGroupSize);
        displayCurrentPage();
        updatePagination();
    }
});
}

// Delete 버튼 이벤트 바인딩
function bindDeleteButtons() {
$(".deleteBtn").off("click").on("click", function() {
    let productId = $(this).closest("tr").find(".productId").text().trim();
    handleDelete(productId);
});
}

// Delete 처리
function handleDelete(productId) {
if (confirm("정말로 이 제품을 삭제하시겠습니까?")) {
    $.ajax({
        url: `/admin/product/delete/${productId}`,
        type: "GET",
        success: function() {
            searchProducts(); // 삭제 후 목록 새로고침
        },
        error: function() {
            alert("제품 삭제 중 오류가 발생했습니다.");
        }
    });
}
}

// Edit 처리
function handleEdit(button) {
let row = $(button).closest("tr");
$("#exampleModalLabel").text("Edit Product");
$("#modalIndex").val(row.find(".productId").text().trim());
$("#modalName").val(row.find(".name").text().trim());
$("#modalWidth").val(row.find(".width").text().trim());
$("#modalDepth").val(row.find(".depth").text().trim());
$("#modalHeight").val(row.find(".height").text().trim());
$("#modalWeight").val(row.find(".weight").text().trim());

let isFragile = row.find(".isFragile").text().trim();
$("#fragileCheckbox").prop("checked", isFragile === "Yes");

let category = row.find(".category").text().trim();
$("#modalCategory").val(category);

$("#exampleModal").modal("show");
}

// Submit 처리
function handleSubmit() {
let product = {
    productId: $("#modalIndex").val().trim(),
    name: $("#modalName").val().trim(),
    width: $("#modalWidth").val().trim(),
    depth: $("#modalDepth").val().trim(),
    height: $("#modalHeight").val().trim(),
    weight: $("#modalWeight").val().trim(),
    category: $("#modalCategory").val().trim(),
    isFragile: $("#fragileCheckbox").is(":checked"),
    
};

if (!validateProduct(product)) {
    alert("모든 필드를 입력해주세요.");
    return;
}

if (product.productId) {
    updateProduct(product);
} else {
    addProduct(product);
}
}

// 제품 추가
function addProduct(product) {
$.ajax({
    url: "/admin/product/add",
    type: "POST",
    data: product,
    success: function() {
        $("#exampleModal").modal("hide");
        searchProducts();
    },
    error: function() {
        alert("제품 추가 중 오류가 발생했습니다.");
    }
});
}

// 제품 수정
function updateProduct(product) {
$.ajax({
    url: "/admin/product/update",
    type: "POST",
    data: product,
    success: function() {
        $("#exampleModal").modal("hide");
        searchProducts();
    },
    error: function() {
        alert("제품 수정 중 오류가 발생했습니다.");
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




document.addEventListener("DOMContentLoaded",
    function() {
        var currentPage = 1;
        var rowsPerPage = 10; // 한 페이지당 표시할 행 수
        var pageGroupSize = 10; // 페이지 번호 그룹 크기

        var table = document.getElementById("productTable");
        var tbody = table.querySelector("tbody");
        var rows = Array.from(tbody
                .getElementsByTagName("tr"));
        var totalPages = Math.ceil(rows.length / rowsPerPage);
        
        function updateTable() {
            rows.forEach(function(row) {
                row.style.display = "none";
            });
            var startIndex = (currentPage - 1)
                    * rowsPerPage;
            var endIndex = Math.min(startIndex
                    + rowsPerPage, rows.length);
            for (var i = startIndex; i < endIndex; i++) {
                rows[i].style.display = "";
            }
        }

        function updatePagination() {
            var pageNumbersElement = document
                    .getElementById("pageNumbers");
            pageNumbersElement.innerHTML = "";
            var startPage = Math.floor((currentPage - 1)
                    / pageGroupSize)
                    * pageGroupSize + 1;
            var endPage = Math.min(startPage
                    + pageGroupSize - 1, totalPages);
            for (var i = startPage; i <= endPage; i++) {
                var btn = document.createElement("button");
                btn.innerText = i;
                if (i === currentPage) {
                    btn.classList.add("active");
                }
                btn.addEventListener("click", (function(
                        page) {
                    return function() {
                        currentPage = page;
                        updateTable();
                        updatePagination();
                    };
                })(i));
                pageNumbersElement.appendChild(btn);
            }
        }

        // < 페이지 이동 버튼 클릭시 이벤트
        document
                .getElementById("prevGroup")
                .addEventListener(
                        "click",
                        function() {
                            var startPage = Math
                                    .floor((currentPage - 1)
                                            / pageGroupSize)
                                    * pageGroupSize + 1;
                            if (startPage > 1) {
                                currentPage = startPage - 1;
                                updateTable();
                                updatePagination();
                            }
                        });
        
        // > 페이지 이동 버튼 클릭시 이벤트
        document
                .getElementById("nextGroup")
                .addEventListener(
                        "click",
                        function() {
                            var startPage = Math
                                    .floor((currentPage - 1)
                                            / pageGroupSize)
                                    * pageGroupSize + 1;
                            var endPage = Math
                                    .min(startPage
                                            + pageGroupSize
                                            - 1, totalPages);
                            if (endPage < totalPages) {
                                currentPage = endPage + 1;
                                updateTable();
                                updatePagination();
                            }
                        });		
        
        updateTable();
        updatePagination();
    });


// 드롭다운 아이템 선택 함수 수정
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
            if (menuId === 'categoryMenu') {
                selectedCategoryValue = value;
            } else if (menuId === 'fragileMenu') {
                selectedFragileValue = value;
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
        let searchQuery = $("#productNameSearch").val().trim();
        let categoryValue = $("#categoryFilter").val();
        let fragileValue = $("#fragileFilter").val();

        // 필터 정보를 객체로 구성
        let filterInfo = {
            name: searchQuery,
            category: categoryValue,
            fragile: fragileValue
        };

        // AJAX 요청으로 필터링 적용
        $.ajax({
            url: "/admin/product/search",
            type: "GET",
            data: filterInfo,
            success: function(data) {
                updateProductList(data);
            },
            error: function(xhr, status, error) {
                console.error("필터링 오류:", error);
                alert("필터링 중 오류가 발생했습니다.");
            }
        });
    }
}

// 드롭다운 토글 함수 수정
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