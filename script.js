const loginForm = document.getElementById('loginForm');

loginForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const dados = {
        usuario: document.getElementById('usuario').value,
        senha: document.getElementById('senha').value
    };

    try {
        // Envia os dados para o endpoint do Spring Boot
        const resposta = await fetch('http://localhost:8080/usuarios', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });

        if (resposta.ok) {
            const resultado = await resposta.json();
            // Salva o token ou nome retornado pelo Java
            localStorage.setItem('usuarioLogado', resultado.nome);
            window.location.href = "Painel.html";
        } else {
            alert("Usuário ou senha inválidos!");
        }
    } catch (erro) {
        console.error("Erro ao conectar com o servidor Java:", erro);
    }
});