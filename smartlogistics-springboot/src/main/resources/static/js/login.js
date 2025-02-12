function openLoginModal(role) {
    document.getElementById("role").value = role;
    document.getElementById("loginModalLabel").innerText = 
        role === "admin" ? "관리자 로그인" : "작업자 로그인";
    new bootstrap.Modal(document.getElementById("loginModal")).show();
}