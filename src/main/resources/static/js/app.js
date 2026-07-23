function readCookie(name) {
    const match = document.cookie.match(new RegExp('(?:^|; )' + name + '=([^;]*)'));
    return match ? decodeURIComponent(match[1]) : null;
}

function csrfHeaders() {
    return { 'X-XSRF-TOKEN': readCookie('XSRF-TOKEN') || '' };
}

document.getElementById('logout-csrf-token').value = readCookie('XSRF-TOKEN') || '';

const TYPE_LABELS = { FILM: 'Film', DOCUMENTAIRE: 'Documentaire', SERIE: 'Série' };
const GENRE_LABELS = {
    NON_CLASSE: 'Non classé',
    COMEDIE: 'Comédie', DRAME: 'Drame', ACTION: 'Action', AVENTURE: 'Aventure',
    HORREUR: 'Horreur', THRILLER: 'Thriller', SCIENCE_FICTION: 'Science-fiction',
    FANTASTIQUE: 'Fantastique', ROMANCE: 'Romance', ANIMATION: 'Animation',
    POLICIER: 'Policier', GUERRE: 'Guerre', HISTORIQUE: 'Historique',
    ANIMALIER: 'Animalier', MUSICAL: 'Musical'
};

function buildOptions(labels, selectedValue) {
    const fragment = document.createDocumentFragment();
    for (const [value, label] of Object.entries(labels)) {
        const option = document.createElement('option');
        option.value = value;
        option.textContent = label;
        option.selected = value === selectedValue;
        fragment.appendChild(option);
    }
    return fragment;
}

const movieList = document.getElementById('movie-list');
const statusMessage = document.getElementById('status-message');
const addForm = document.getElementById('add-form');
const titleInput = document.getElementById('title-input');
const barcodeInput = document.getElementById('barcode-input');
const typeInput = document.getElementById('type-input');
const genreInput = document.getElementById('genre-input');
const yearInput = document.getElementById('year-input');
const actorsInput = document.getElementById('actors-input');
const searchInput = document.getElementById('search-input');
const typeFilter = document.getElementById('type-filter');
const genreFilter = document.getElementById('genre-filter');
const browseView = document.getElementById('browse-view');
const enrichView = document.getElementById('enrich-view');
const browseTab = document.getElementById('browse-tab');
const enrichTab = document.getElementById('enrich-tab');

function showStatus(message, isError) {
    statusMessage.textContent = message;
    statusMessage.style.color = isError ? 'var(--danger)' : 'var(--text-muted)';
}

function currentFilters() {
    return { title: searchInput.value, type: typeFilter.value, genre: genreFilter.value };
}

function showView(view) {
    const isBrowse = view === 'browse';
    browseView.hidden = !isBrowse;
    enrichView.hidden = isBrowse;
    browseTab.classList.toggle('active', isBrowse);
    enrichTab.classList.toggle('active', !isBrowse);
    if (isBrowse) {
        loadMovies(currentFilters());
    }
}

browseTab.addEventListener('click', () => showView('browse'));
enrichTab.addEventListener('click', () => showView('enrich'));

let lastMovies = [];
let editingId = null;

function buildDisplayRow(movie) {
    const row = document.createElement('div');
    row.className = 'movie-row' + (movie.watched ? ' watched' : '');

    const info = document.createElement('div');
    info.className = 'movie-info';

    const title = document.createElement('span');
    title.className = 'movie-title';
    title.textContent = movie.title;

    const meta = document.createElement('span');
    meta.className = 'movie-meta';
    const metaParts = [TYPE_LABELS[movie.type] || movie.type, GENRE_LABELS[movie.genre] || movie.genre, movie.year];
    meta.textContent = metaParts.join(' · ');

    const barcode = document.createElement('span');
    barcode.className = 'movie-barcode';
    barcode.textContent = movie.barcode;

    info.append(title, meta, barcode);

    if (movie.actors && movie.actors.length > 0) {
        const actors = document.createElement('span');
        actors.className = 'movie-actors';
        actors.textContent = 'Avec ' + movie.actors.join(', ');
        info.appendChild(actors);
    }

    const watchedLabel = document.createElement('label');
    watchedLabel.className = 'watched-toggle';
    const watchedCheckbox = document.createElement('input');
    watchedCheckbox.type = 'checkbox';
    watchedCheckbox.checked = movie.watched;
    watchedCheckbox.addEventListener('change', () => toggleWatched(movie, watchedCheckbox.checked));
    const watchedText = document.createElement('span');
    watchedText.textContent = 'Vu';
    watchedLabel.append(watchedCheckbox, watchedText);

    const editButton = document.createElement('button');
    editButton.type = 'button';
    editButton.className = 'row-edit';
    editButton.setAttribute('aria-label', `Éditer ${movie.title}`);
    editButton.textContent = '✎';
    editButton.addEventListener('click', () => {
        editingId = movie.id;
        renderMovies(lastMovies);
    });

    const deleteButton = document.createElement('button');
    deleteButton.type = 'button';
    deleteButton.className = 'row-delete';
    deleteButton.setAttribute('aria-label', `Supprimer ${movie.title}`);
    deleteButton.textContent = '✕';
    deleteButton.addEventListener('click', () => deleteMovie(movie));

    row.append(info, watchedLabel, editButton, deleteButton);
    return row;
}

function buildEditRow(movie) {
    const row = document.createElement('div');
    row.className = 'movie-row movie-row-edit';

    const titleField = document.createElement('input');
    titleField.type = 'text';
    titleField.value = movie.title;
    titleField.placeholder = 'Titre';

    const barcodeField = document.createElement('input');
    barcodeField.type = 'text';
    barcodeField.value = movie.barcode;
    barcodeField.placeholder = 'Code-barre';

    const typeField = document.createElement('select');
    typeField.appendChild(buildOptions(TYPE_LABELS, movie.type));

    const genreField = document.createElement('select');
    genreField.appendChild(buildOptions(GENRE_LABELS, movie.genre));

    const yearField = document.createElement('input');
    yearField.type = 'number';
    yearField.min = '1888';
    yearField.max = '2100';
    yearField.value = movie.year;

    const actorsField = document.createElement('input');
    actorsField.type = 'text';
    actorsField.placeholder = 'Acteurs (séparés par des virgules)';
    actorsField.value = (movie.actors || []).join(', ');

    const saveButton = document.createElement('button');
    saveButton.type = 'button';
    saveButton.textContent = 'Enregistrer';
    saveButton.addEventListener('click', () => saveEdit(movie.id, {
        title: titleField.value,
        barcode: barcodeField.value,
        watched: movie.watched,
        type: typeField.value,
        genre: genreField.value,
        year: Number(yearField.value),
        actors: actorsField.value.split(',').map(a => a.trim()).filter(a => a.length > 0)
    }));

    const cancelButton = document.createElement('button');
    cancelButton.type = 'button';
    cancelButton.className = 'secondary';
    cancelButton.textContent = 'Annuler';
    cancelButton.addEventListener('click', () => {
        editingId = null;
        renderMovies(lastMovies);
    });

    row.append(titleField, barcodeField, typeField, genreField, yearField, actorsField, saveButton, cancelButton);
    return row;
}

function renderMovies(movies) {
    lastMovies = movies;
    movieList.innerHTML = '';

    if (movies.length === 0) {
        const empty = document.createElement('p');
        empty.className = 'empty-state';
        empty.textContent = "Aucun film ne correspond — l'étagère attend son premier Blu-ray !";
        movieList.appendChild(empty);
        return;
    }

    for (const movie of movies) {
        movieList.appendChild(movie.id === editingId ? buildEditRow(movie) : buildDisplayRow(movie));
    }
}

async function saveEdit(id, body) {
    const response = await fetch(`/api/movies/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', ...csrfHeaders() },
        body: JSON.stringify(body)
    });
    if (response.status === 409) {
        showStatus('Ce code-barre existe déjà dans la collection.', true);
        return;
    }
    if (!response.ok) {
        showStatus('Impossible de mettre à jour ce film.', true);
        return;
    }
    editingId = null;
    showStatus(`« ${body.title} » mis à jour.`, false);
    await loadMovies(currentFilters());
}

async function loadMovies(filters) {
    const params = new URLSearchParams();
    if (filters.title) params.set('title', filters.title);
    if (filters.type) params.set('type', filters.type);
    if (filters.genre) params.set('genre', filters.genre);
    const query = params.toString() ? `?${params.toString()}` : '';

    const response = await fetch(`/api/movies${query}`);
    if (!response.ok) {
        showStatus('Impossible de charger la collection.', true);
        return;
    }
    renderMovies(await response.json());
}

let searchDebounce;
searchInput.addEventListener('input', () => {
    clearTimeout(searchDebounce);
    searchDebounce = setTimeout(() => loadMovies(currentFilters()), 250);
});
typeFilter.addEventListener('change', () => loadMovies(currentFilters()));
genreFilter.addEventListener('change', () => loadMovies(currentFilters()));

function movieRequestBody(movie) {
    return {
        title: movie.title,
        barcode: movie.barcode,
        watched: movie.watched,
        type: movie.type,
        genre: movie.genre,
        year: movie.year,
        actors: movie.actors || []
    };
}

async function toggleWatched(movie, watched) {
    const response = await fetch(`/api/movies/${movie.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', ...csrfHeaders() },
        body: JSON.stringify({ ...movieRequestBody(movie), watched })
    });
    if (!response.ok) {
        showStatus('Impossible de mettre à jour ce film.', true);
        return;
    }
    await loadMovies(currentFilters());
}

async function deleteMovie(movie) {
    const response = await fetch(`/api/movies/${movie.id}`, {
        method: 'DELETE',
        headers: csrfHeaders()
    });
    if (!response.ok) {
        showStatus('Impossible de supprimer ce film.', true);
        return;
    }
    showStatus(`« ${movie.title} » supprimé.`, false);
    await loadMovies(currentFilters());
}

addForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const actors = actorsInput.value.split(',').map(a => a.trim()).filter(a => a.length > 0);

    const response = await fetch('/api/movies', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...csrfHeaders() },
        body: JSON.stringify({
            title: titleInput.value,
            barcode: barcodeInput.value,
            watched: false,
            type: typeInput.value,
            genre: genreInput.value,
            year: Number(yearInput.value),
            actors
        })
    });
    if (response.status === 409) {
        showStatus('Ce code-barre existe déjà dans la collection.', true);
        return;
    }
    if (!response.ok) {
        showStatus("Impossible d'ajouter ce film.", true);
        return;
    }
    showStatus(`« ${titleInput.value} » ajouté.`, false);
    addForm.reset();
});

showView('browse');
