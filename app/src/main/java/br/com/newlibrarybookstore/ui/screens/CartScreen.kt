// file: ui/screens/CartScreen.kt
package br.com.newlibrarybookstore.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.newlibrarybookstore.data.Book
import br.com.newlibrarybookstore.ui.viewmodel.CartItem
import br.com.newlibrarybookstore.ui.viewmodel.CartViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel = viewModel(),
    onCheckout: () -> Unit
) {
    val cartItemsMap by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()

    val cartItemsList = cartItemsMap.values.toList().sortedBy { it.book.title }

    if (cartItemsList.isEmpty()) {
        // TELA DE CARRINHO VAZIO
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "O carrinho está vazio :(",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }
    } else {
        // TELA DE CARRINHO COM ITENS
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Itens do carrinho de compras:", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItemsList) { cartItem ->
                    // ATUALIZE A CHAMADA PARA CartListItem AQUI:
                    CartListItem(
                        item = cartItem,
                        // Passa a função do ViewModel para incrementar
                        onIncrease = { book -> cartViewModel.addToCart(book) },
                        // Passa a função do ViewModel para decrementar
                        onDecrease = { book -> cartViewModel.removeFromCart(book) }
                    )
                    Divider()
                }
            }
            // RODAPÉ COM TOTAL E BOTÕES
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total:", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "R$ %.2f".format(totalPrice),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Finalizar compra")
                    }
                    // BOTÃO DE ESVAZIAR O CARRINHO
                    OutlinedButton(
                        onClick = { cartViewModel.clearCart() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Esvaziar o carrinho")
                    }
                }
            }
        }
    }
}

@Composable
fun CartListItem(
    item: CartItem,
    onIncrease: (Book) -> Unit,
    onDecrease: (Book) -> Unit
) {
    // Calculamos o preço total para esta linha (ex: 2 x R$15,00 = R$30,00)
    val linePrice = (item.book.price * item.quantity) / 100.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coluna para o Título e Autor
        Column(modifier = Modifier.weight(1f)) {
            Text(item.book.title, fontWeight = FontWeight.Bold, maxLines = 2)
            Text(item.book.author, style = MaterialTheme.typography.bodySmall)
        }

        // Coluna para os controles de quantidade e preço
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("R$ %.2f".format(linePrice), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Botão de DECREMENTAR (-)
                IconButton(
                    onClick = { onDecrease(item.book) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Remover um")
                }

                Text(
                    text = "${item.quantity}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Botão de INCREMENTAR (+)
                IconButton(
                    onClick = { onIncrease(item.book) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Adicionar mais um")
                }
            }
        }
    }
}