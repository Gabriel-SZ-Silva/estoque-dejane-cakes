// Service worker do app no celular (PWA).
// Guarda só os arquivos visuais (estilo, logo, ícones) e a página "sem conexão".
// As telas com dados do estoque nunca ficam guardadas no aparelho: sempre vêm do servidor.
const CACHE = 'dejane-cakes-v3';
const ARQUIVOS = ['css/estilo.css', 'img/logo.jpg', 'img/icone-192.png', 'img/icone-512.png', 'offline.html'];

self.addEventListener('install', evento => {
    evento.waitUntil(caches.open(CACHE).then(cache => cache.addAll(ARQUIVOS)));
    self.skipWaiting();
});

// Ao mudar este arquivo ou a lista ARQUIVOS, troque o nome do CACHE (v3 -> v4) para os celulares se atualizarem.
self.addEventListener('activate', evento => {
    evento.waitUntil(caches.keys().then(nomes =>
        Promise.all(nomes.filter(nome => nome !== CACHE).map(nome => caches.delete(nome)))));
    self.clients.claim();
});

self.addEventListener('fetch', evento => {
    const pedido = evento.request;
    if (pedido.method !== 'GET') {
        return;
    }
    // Abrir uma tela: tenta o servidor; sem internet, mostra a página "sem conexão".
    if (pedido.mode === 'navigate') {
        evento.respondWith(fetch(pedido).catch(() => caches.match('offline.html')));
        return;
    }
    // Estilo e imagens: busca no servidor e atualiza a cópia guardada; sem internet, usa a cópia.
    // Assim uma mudança no estilo aparece logo, sem depender de trocar o nome do CACHE.
    const endereco = new URL(pedido.url);
    if (endereco.origin === self.location.origin && (endereco.pathname.includes('/css/') || endereco.pathname.includes('/img/'))) {
        evento.respondWith(
            fetch(pedido).then(resposta => {
                if (resposta.ok) {
                    const copia = resposta.clone();
                    caches.open(CACHE).then(cache => cache.put(pedido, copia));
                }
                return resposta;
            }).catch(() => caches.match(pedido)));
    }
});
