package br.com.newlibrarybookstore.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.newlibrarybookstore.R
import br.com.newlibrarybookstore.data.Book
import br.com.newlibrarybookstore.ui.viewmodel.BookListViewModel
import br.com.newlibrarybookstore.ui.viewmodel.CartViewModel
import coil.compose.AsyncImage

@Composable
fun BookStoreScreen(
    bookListViewModel: BookListViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    onBookClick: (Book) -> Unit
) {
    val books by bookListViewModel.books.collectAsState()
    if (books.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(books) { book ->
                BookStoreItem(
                    book = book,
                    onAddToCart = { cartViewModel.addToCart(book) },
                    onClick = { onBookClick(book) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BookStoreItem(book: Book, onAddToCart: (Book) -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            // Usamos a nova propriedade 'coverImageUrl'
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = "Capa do livro ${book.title}",
                // Adicionamos um placeholder para o caso da imagem não carregar ou ser nula
                placeholder = painterResource(id = R.drawable.ic_launcher_background), // Use um drawable seu
                error = painterResource(id = R.drawable.ic_launcher_background), // Use um drawable seu
                modifier = Modifier.size(100.dp, 150.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3 // Evita que títulos muito longos quebrem o layout
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Exibimos o preço formatado que criamos no modelo
                Text(
                    text = book.formattedPrice,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.weight(1f)) // Empurra o botão para baixo

                Button(
                    onClick = { onAddToCart(book) },
                    modifier = Modifier.align(Alignment.End) // Alinha o botão à direita
                ) {
                    Text("Comprar")
                }
            }
        }
    }
}