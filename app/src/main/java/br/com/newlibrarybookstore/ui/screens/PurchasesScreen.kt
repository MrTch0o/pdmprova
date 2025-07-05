package br.com.newlibrarybookstore.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.newlibrarybookstore.data.BookSale
import br.com.newlibrarybookstore.ui.viewmodel.PurchasesViewModel

@Composable
fun PurchasesScreen(purchasesViewModel: PurchasesViewModel = viewModel()) {
    val purchasedItems by purchasesViewModel.purchases.collectAsState()

    if (purchasedItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Você ainda não fez nenhuma compra. aqui")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(purchasedItems) { purchaseRecord ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Data da compra: ${purchaseRecord.date}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    purchaseRecord.items.forEach { bookSale ->
                        PurchasedBookItem(bookSale = bookSale)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp)) // espaço entre compras
                }
            }
        }
    }
}

// NOVO COMPOSABLE: Apenas para a tela de histórico de compras.
// Ele não mostra "Unidades" nem o botão "Comprar".
@Composable
fun PurchasedBookItem(bookSale: BookSale) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    bookSale.bookTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Quantidade: ${bookSale.unities}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "R$ %.2f".format(bookSale.bookPrice * bookSale.unities / 100.0),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
