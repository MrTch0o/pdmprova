// file: ui/screens/BookDetailsScreen.kt
package br.com.newlibrarybookstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import br.com.newlibrarybookstore.ui.viewmodel.BookDetailsViewModel
import br.com.newlibrarybookstore.ui.viewmodel.CartViewModel

@Composable
fun BookDetailsScreen(
    bookId: String,
    detailsViewModel: BookDetailsViewModel = viewModel(),
    cartViewModel: CartViewModel // Recebemos o mesmo CartViewModel da Activity
) {
    // Dispara a busca do livro apenas uma vez quando a tela é criada
    LaunchedEffect(key1 = bookId) {
        detailsViewModel.fetchBookById(bookId)
    }

    val book by detailsViewModel.book.collectAsState()
    val isLoading by detailsViewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (book != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = book!!.coverImageUrl,
                    contentDescription = "Capa do livro ${book!!.title}",
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = book!!.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = book!!.author,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f)) // Empurra o botão para baixo
                Button(
                    onClick = { cartViewModel.addToCart(book!!) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adicionar ao Carrinho")
                }
            }
        } else {
            Text("Livro não encontrado ou erro ao carregar.", modifier = Modifier.align(Alignment.Center))
        }
    }
}