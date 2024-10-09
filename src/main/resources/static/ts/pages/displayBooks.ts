// Define the Book interface
interface Book {
    id: number;
    cover: string | null;
    title: string;
    author: string;
    year: number;
    publisher: string;
    userId: number;
}

async function fetchBooks(): Promise<void> {
    try {
        const response = await fetch('http://localhost:8080/api/books');
        if (!response.ok) {
            throw new Error(`An error occurred: ${response.statusText}`);
        }

        const books: Book[] = await response.json(); // Use the Book interface
        console.log('Fetched books:', books); // Log the fetched data
        await displayBooks(books);
    } catch (error) {
        console.error('Failed to fetch books:', error);
    }
}

async function displayBooks(books: Book[]): Promise<void> { // Specify type for books
    const booksList = document.getElementById('books-list');

    if (booksList) {
        booksList.innerHTML = ''; // Clear existing content

        for (const book of books) {
            console.log('Displaying book:', book); // Log each book being displayed

            // If the cover is null, fetch it from Google Books API
            let coverImageUrl = book.cover;
            if (!coverImageUrl) {
                coverImageUrl = await fetchCoverImage(book.title, book.author);
            }

            const bookCard = document.createElement('div');
            bookCard.className = 'bg-white p-4 rounded-lg shadow-md';

            bookCard.innerHTML = `
                <img src="${coverImageUrl || 'https://via.placeholder.com/150'}" alt="Cover of ${book.title}" class="w-full h-48 object-cover mb-4 rounded-md">
                <h2 class="text-xl font-semibold">${book.title}</h2>
                <p class="text-gray-600">Author: ${book.author}</p>
                <p class="text-gray-600">Year: ${book.year}</p>
                <p class="text-gray-600">Publisher: ${book.publisher}</p>
            `;

            booksList.appendChild(bookCard);
        }
    } else {
        console.error('Element with ID "books-list" not found.');
    }
}

async function fetchCoverImage(title: string, author: string): Promise<string | null> { // Specify types for parameters
    const query = `${title} ${author}`.replace(/\s+/g, '+');
    const url = `https://www.googleapis.com/books/v1/volumes?q=${query}&maxResults=1`;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Failed to fetch cover image: ${response.statusText}`);
        }

        const data = await response.json();
        if (data.items && data.items.length > 0) {
            return data.items[0].volumeInfo.imageLinks?.thumbnail || null; // Return the thumbnail image link
        }
    } catch (error) {
        console.error('Error fetching cover image:', error);
    }
    return null; // Return null if no image found
}

// Call the function when the page loads
document.addEventListener('DOMContentLoaded', fetchBooks);
