// Global Search Functionality
let searchTimeout;

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('globalSearchInput');
    const searchResults = document.getElementById('searchResults');

    if (!searchInput) return;

    // Handle input changes
    searchInput.addEventListener('input', function() {
        clearTimeout(searchTimeout);
        const query = this.value.trim();

        if (query.length < 2) {
            searchResults.style.display = 'none';
            return;
        }

        // Debounce search requests
        searchTimeout = setTimeout(() => {
            performSearch(query);
        }, 300);
    });

    // Close dropdown when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.position-relative') && !e.target.closest('#globalSearchInput')) {
            searchResults.style.display = 'none';
        }
    });

    // Allow keyboard navigation
    searchInput.addEventListener('keydown', function(e) {
        const items = searchResults.querySelectorAll('a');
        if (items.length === 0) return;

        if (e.key === 'ArrowDown') {
            e.preventDefault();
            items[0].focus();
        }
    });
});

function performSearch(query) {
    const searchResults = document.getElementById('searchResults');

    fetch(`/api/search?q=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(data => {
            displayResults(data);
        })
        .catch(error => console.error('Error fetching search results:', error));
}

function displayResults(data) {
    const searchResults = document.getElementById('searchResults');
    
    if (!data.mascotas && !data.propietarios && !data.historiales) {
        searchResults.innerHTML = '<div class="p-3 text-center text-muted">No se encontraron resultados</div>';
        searchResults.style.display = 'block';
        return;
    }

    let html = '';

    // Mascotas
    if (data.mascotas && data.mascotas.length > 0) {
        html += '<div class="px-2 py-2">';
        html += '<h6 class="text-muted small ps-2 mb-2">🐾 Mascotas</h6>';
        data.mascotas.forEach(mascota => {
            html += `<a href="${mascota.url}" class="d-block px-3 py-2 text-decoration-none text-dark rounded hover-item" style="cursor: pointer;">
                        <strong>${mascota.titulo}</strong>
                        <small class="d-block text-muted">${mascota.subtitulo}</small>
                    </a>`;
        });
        html += '</div>';
    }

    // Propietarios
    if (data.propietarios && data.propietarios.length > 0) {
        html += '<div class="px-2 py-2">';
        html += '<h6 class="text-muted small ps-2 mb-2">👤 Dueños</h6>';
        data.propietarios.forEach(propietario => {
            html += `<a href="${propietario.url}" class="d-block px-3 py-2 text-decoration-none text-dark rounded hover-item" style="cursor: pointer;">
                        <strong>${propietario.titulo}</strong>
                        <small class="d-block text-muted">${propietario.subtitulo}</small>
                    </a>`;
        });
        html += '</div>';
    }

    // Historiales (Consultas)
    if (data.historiales && data.historiales.length > 0) {
        html += '<div class="px-2 py-2">';
        html += '<h6 class="text-muted small ps-2 mb-2">📋 Consultas</h6>';
        data.historiales.forEach(historial => {
            html += `<a href="${historial.url}" class="d-block px-3 py-2 text-decoration-none text-dark rounded hover-item" style="cursor: pointer;">
                        <strong>${historial.titulo}</strong>
                        <small class="d-block text-muted">${historial.subtitulo}</small>
                    </a>`;
        });
        html += '</div>';
    }

    if (html === '') {
        html = '<div class="p-3 text-center text-muted">No se encontraron resultados</div>';
    }

    searchResults.innerHTML = html;
    searchResults.style.display = 'block';

    // Add hover effect
    searchResults.querySelectorAll('.hover-item').forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.backgroundColor = '#f0f0f0';
        });
        item.addEventListener('mouseleave', function() {
            this.style.backgroundColor = 'transparent';
        });
    });
}
