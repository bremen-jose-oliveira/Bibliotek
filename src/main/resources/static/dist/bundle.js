/******/ (() => { // webpackBootstrap
/******/ 	"use strict";
/******/ 	var __webpack_modules__ = ({

/***/ "./src/main/resources/static/ts/pages/displayBooks.ts":
/*!************************************************************!*\
  !*** ./src/main/resources/static/ts/pages/displayBooks.ts ***!
  \************************************************************/
/***/ (function() {


var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
function fetchBooks() {
    return __awaiter(this, void 0, void 0, function* () {
        try {
            const response = yield fetch('http://localhost:8080/api/books');
            if (!response.ok) {
                throw new Error(`An error occurred: ${response.statusText}`);
            }
            const books = yield response.json();
            console.log('Fetched books:', books);
            yield displayBooks(books);
        }
        catch (error) {
            console.error('Failed to fetch books:', error);
        }
    });
}
function displayBooks(books) {
    return __awaiter(this, void 0, void 0, function* () {
        const booksList = document.getElementById('books-list');
        if (booksList) {
            booksList.innerHTML = '';
            for (const book of books) {
                console.log('Displaying book:', book);
                let coverImageUrl = book.cover;
                if (!coverImageUrl) {
                    coverImageUrl = yield fetchCoverImage(book.title, book.author);
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
        }
        else {
            console.error('Element with ID "books-list" not found.');
        }
    });
}
function fetchCoverImage(title, author) {
    return __awaiter(this, void 0, void 0, function* () {
        var _a;
        const query = `${title} ${author}`.replace(/\s+/g, '+');
        const url = `https://www.googleapis.com/books/v1/volumes?q=${query}&maxResults=1`;
        try {
            const response = yield fetch(url);
            if (!response.ok) {
                throw new Error(`Failed to fetch cover image: ${response.statusText}`);
            }
            const data = yield response.json();
            if (data.items && data.items.length > 0) {
                return ((_a = data.items[0].volumeInfo.imageLinks) === null || _a === void 0 ? void 0 : _a.thumbnail) || null;
            }
        }
        catch (error) {
            console.error('Error fetching cover image:', error);
        }
        return null;
    });
}
// Initialize the book fetch on DOM content loaded
document.addEventListener('DOMContentLoaded', fetchBooks);


/***/ })

/******/ 	});
/************************************************************************/
/******/ 	// The module cache
/******/ 	var __webpack_module_cache__ = {};
/******/ 	
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/ 		// Check if module is in cache
/******/ 		var cachedModule = __webpack_module_cache__[moduleId];
/******/ 		if (cachedModule !== undefined) {
/******/ 			return cachedModule.exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = __webpack_module_cache__[moduleId] = {
/******/ 			// no module.id needed
/******/ 			// no module.loaded needed
/******/ 			exports: {}
/******/ 		};
/******/ 	
/******/ 		// Execute the module function
/******/ 		__webpack_modules__[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/ 	
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/ 	
/************************************************************************/
var __webpack_exports__ = {};
// This entry need to be wrapped in an IIFE because it need to be isolated against other modules in the chunk.
(() => {
var exports = __webpack_exports__;
/*!***********************************************!*\
  !*** ./src/main/resources/static/ts/index.ts ***!
  \***********************************************/

Object.defineProperty(exports, "__esModule", ({ value: true }));
const displayBooks_1 = __webpack_require__(/*! ./pages/displayBooks */ "./src/main/resources/static/ts/pages/displayBooks.ts");
// Initialize event listeners when the DOM content is loaded
document.addEventListener('DOMContentLoaded', () => {
    (0, displayBooks_1.initializeEventListeners)();
});

})();

/******/ })()
;
//# sourceMappingURL=bundle.js.map