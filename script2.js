const usuario = localStorage.getItem('usuarioLogado');

    if (!usuario) {
        window.location.href = "Tela de login.html";
    } else {
        document.getElementById('boas-vindas').textContent = `Bem-vindo(a), ${usuario}!`;
    }

    document.getElementById('btnSair').addEventListener('click', () => {
        localStorage.removeItem('usuarioLogado');
        window.location.href = "Tela de login.html";
    });