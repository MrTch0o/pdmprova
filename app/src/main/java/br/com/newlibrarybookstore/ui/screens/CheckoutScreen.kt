package br.com.newlibrarybookstore.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.newlibrarybookstore.ui.viewmodel.CartViewModel
import br.com.newlibrarybookstore.ui.viewmodel.PurchasesViewModel
import kotlinx.coroutines.launch

fun base64ToBitmap(base64String: String) = try {
    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
} catch (e: Exception) {
    null
}

@Composable
fun CheckoutScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {

    val sale = cartViewModel.currentSale.collectAsState().value
    if (sale == null) {
        Text("Erro ao carregar checkout AQUI.")
        return
    }
    Log.d("SaleDebug", "Checkout: $sale")
    val context = LocalContext.current
    val purchasesViewModel: PurchasesViewModel = viewModel()
    val bitmap = base64ToBitmap(sale.pixB64.substringAfter(','))
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        bitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "QR Code PIX", modifier = Modifier.size(250.dp).padding(16.dp))
        } ?: Text("Erro ao carregar QR Code", color = MaterialTheme.colorScheme.error)

        Text("Pix QRCode:", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = sale.pixStr,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().height(70.dp).padding(vertical = 4.dp).horizontalScroll(rememberScrollState()),
            readOnly = true,
            maxLines = 2
        )

        Button(onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("pix", sale.pixStr))
            Toast.makeText(context, "Pix Copiado!", Toast.LENGTH_SHORT).show()
        }) { Text("Copiar") }

        Text("Total: ${sale.formattedTotal}", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                scope.launch {
                    if (cartViewModel.confirmSale(sale.uuid)) {
                        purchasesViewModel.addPurchase(sale.booksSales)
                        cartViewModel.clearCart()
                        navController.navigate("purchases") { popUpTo("store") }
                    } else {
                        Toast.makeText(context, "Erro ao confirmar pagamento!", Toast.LENGTH_SHORT).show()
                    }
                }
            }) { Text("Confirmar Pagamento") }

            OutlinedButton(onClick = {
                scope.launch {
                    if (cartViewModel.cancelSale(sale.uuid)) {
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "Erro ao cancelar pagamento!", Toast.LENGTH_SHORT).show()
                    }
                }
            }) { Text("Cancelar") }
        }
    }
}
