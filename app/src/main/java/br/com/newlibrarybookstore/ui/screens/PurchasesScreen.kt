package br.com.newlibrarybookstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import br.com.newlibrarybookstore.data.Book
import br.com.newlibrarybookstore.ui.viewmodel.CartViewModel

@Composable
fun PurchasesScreen(cartViewModel: CartViewModel = viewModel()) {
    val purchasedItems by cartViewModel.purchasedItems.collectAsState()

    if (purchasedItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Você ainda não fez nenhuma compra.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(purchasedItems) { book ->
                // Usamos nosso novo Composable simplificado
                PurchasedBookItem(book = book)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// NOVO COMPOSABLE: Apenas para a tela de histórico de compras.
// Ele não mostra "Unidades" nem o botão "Comprar".
@Composable
fun PurchasedBookItem(book: Book) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = "Capa do livro ${book.title}",
                modifier = Modifier.size(80.dp, 120.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(book.author, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Editora: ${book.publisher}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "Ano: ${book.year}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = book.formattedPrice,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
