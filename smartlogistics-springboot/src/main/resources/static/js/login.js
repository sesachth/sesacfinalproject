function openLoginModal(role) {
    document.getElementById("role").value = role;
    document.getElementById("loginModalLabel").innerText = 
        role === "admin" ? "관리자 로그인" : "작업자 로그인";
    new bootstrap.Modal(document.getElementById("loginModal")).show();
}

// 모달 닫힐 때 입력 필드 및 에러 메시지 초기화
document.getElementById("loginModal").addEventListener("hidden.bs.modal", function () {
    document.getElementById("loginForm").reset();  // 입력 필드 초기화
    document.getElementById("error-message").style.display = "none"; // 에러 메시지 숨기기
});

// 로그인 폼 제출 이벤트 처리
document.getElementById("loginForm").addEventListener("submit", function(event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const formData = new FormData(this);

    fetch("/perform_login", {
        method: "POST",
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => { throw new Error(data.error); });
        }
        return response.text(); // 서버 응답 (URL 경로)
    })
    .then(url => {
        window.location.href = url; // 로그인 성공 시 리다이렉트
    })
    .catch(error => {
        document.getElementById("error-message").innerText = error.message;
        document.getElementById("error-message").style.display = "block";
    });
});