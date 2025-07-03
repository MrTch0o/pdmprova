package br.com.newlibrarybookstore.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.newlibrarybookstore.data.Book
import br.com.newlibrarybookstore.data.Sale
import br.com.newlibrarybookstore.ui.viewmodel.CartItem
import br.com.newlibrarybookstore.ui.viewmodel.CartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItemsMap by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()

    val cartItemsList = cartItemsMap.values.toList().sortedBy { it.book.title }

    if (cartItemsList.isEmpty()) {
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
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text("Itens do carrinho de compras:", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItemsList) { cartItem ->
                    CartListItem(
                        item = cartItem,
                        onIncrease = { cartViewModel.addToCart(it) },
                        onDecrease = { cartViewModel.removeFromCart(it) }
                    )
                    HorizontalDivider()
                }
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
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
                        onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                val sale: Sale? = cartViewModel.createSale()
                                if (sale != null) {
                                    cartViewModel.setCurrentSale(sale)
                                    Log.d("CheckoutDebug", "Checkout response sale checkout: $sale")
                                    navController.navigate("checkout")
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "Erro ao gerar checkout, tente novamente.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Checkout") }

                    OutlinedButton(
                        onClick = { cartViewModel.clearCart() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Descartar") }
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
    val linePrice = (item.book.price * item.quantity) / 100.0
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.book.title, fontWeight = FontWeight.Bold, maxLines = 2)
            Text(item.book.author, style = MaterialTheme.typography.bodySmall)
        }
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
            Text("R$ %.2f".format(linePrice), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onDecrease(item.book) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Remover")
                }
                Text("${item.quantity}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { onIncrease(item.book) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Adicionar")
                }
            }
        }
    }
}
