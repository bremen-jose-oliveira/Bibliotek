// addBookForm.ts
interface BookData {
    title: string;
    author: string;
    year: number;
    publisher: string;
}

// Elements
const searchQueryInput = document.getElementById('search-query') as HTMLInputElement;
const searchResultsContainer = document.getElementById('search-results') as HTMLDivElement;
const addBookForm = document.getElementById('add-book-form') as HTMLFormElement;

// Fetch book data from Google Books API
async function fetchBookData(): Promise<void> {
    const query = searchQueryInput.value.trim();
    if (!query) {
        searchResultsContainer.innerHTML = '';
        searchResultsContainer.classList.add('hidden');
        return;
    }

    const googleBooksApiUrl = `https://www.googleapis.com/books/v1/volumes?q=${encodeURIComponent(query)}`;

    try {
        const response = await fetch(googleBooksApiUrl);
        if (!response.ok) {
            throw new Error(`An error occurred: ${response.statusText}`);
        }
        const data = await response.json();
        displaySearchResults(data.items || []);
    } catch (error) {
        console.error('Failed to fetch book data:', error);
        alert('Failed to fetch book data. Please try again.');
    }
}

// Display search results
function displaySearchResults(books: any[]): void {
    searchResultsContainer.innerHTML = '';
    if (books.length > 0) {
        searchResultsContainer.classList.remove('hidden');

        books.forEach(book => {
            const bookData = book.volumeInfo;
            const bookItem = document.createElement('div');
            bookItem.className = 'p-2 hover:bg-gray-300 cursor-pointer flex items-center';

            bookItem.innerHTML = `
                ${bookData.imageLinks ? `<img src="${bookData.imageLinks.thumbnail}" alt="${bookData.title}" class="w-16 h-24 mr-4">` : ''}
                <div>
                    <div class="font-semibold">${bookData.title}</div>
                    <div class="text-gray-600">${bookData.authors ? bookData.authors.join(', ') : 'Unknown Author'}</div>
                </div>
            `;

            bookItem.addEventListener('click', () => populateForm(bookData));
            searchResultsContainer.appendChild(bookItem);
        });
    } else {
        searchResultsContainer.classList.add('hidden');
    }
}

// Populate the form with selected book data
function populateForm(bookData: any): void {
    (document.getElementById('title') as HTMLInputElement).value = bookData.title || '';
    (document.getElementById('author') as HTMLInputElement).value = bookData.authors ? bookData.authors.join(', ') : '';
    (document.getElementById('year') as HTMLInputElement).value = bookData.publishedDate ? bookData.publishedDate.substring(0, 4) : '';
    (document.getElementById('publisher') as HTMLInputElement).value = bookData.publisher || '';
    searchResultsContainer.innerHTML = '';
    searchResultsContainer.classList.add('hidden');
}

// Listen for input changes to fetch book data
searchQueryInput.addEventListener('input', fetchBookData);

// Handle form submission
addBookForm.addEventListener('submit', async function (event) {
    event.preventDefault();
    const bookData: BookData = {
        title: (document.getElementById('title') as HTMLInputElement).value,
        author: (document.getElementById('author') as HTMLInputElement).value,
        year: parseInt((document.getElementById('year') as HTMLInputElement).value, 10),
        publisher: (document.getElementById('publisher') as HTMLInputElement).value,
    };

    try {
        const response = await fetch('http://localhost:8080/api/books', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(bookData),
        });

        if (response.ok) {
            alert('Book added successfully!');
            addBookForm.reset();
        } else {
            throw new Error(`Failed to add book: ${response.statusText}`);
        }
    } catch (error) {
        console.error('Error adding book:', error);
        alert('Failed to add book. Please try again.');
    }
});

// Initialize event listeners on DOM content loaded
document.addEventListener('DOMContentLoaded', () => {
    searchQueryInput.addEventListener('input', fetchBookData);
});
