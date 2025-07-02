package br.com.newlibrarybookstore.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.newlibrarybookstore.data.Sale
import br.com.newlibrarybookstore.ui.viewmodel.CartViewModel
import br.com.newlibrarybookstore.ui.viewmodel.PurchasesViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.net.URLDecoder

// Função utilitária para converter Base64 em Bitmap
fun base64ToBitmap(base64String: String): Bitmap? {
    return try {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        Log.e("CheckoutDebug", "Erro ao converter Base64 para Bitmap: ${e.message}", e)
        null
    }
}

@Composable
fun CheckoutScreen(
    navController: NavController,
    saleJson: String,
    cartViewModel: CartViewModel = viewModel()
) {
    val context = LocalContext.current
    val purchasesViewModel: PurchasesViewModel = viewModel()
    // Decodifica o JSON recebido
    val saleJsonDecoded = URLDecoder.decode(saleJson, "UTF-8")
    val sale = Gson().fromJson(saleJsonDecoded, Sale::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Scroll adicionado corretamente
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Usando QR Code mock temporariamente para seguir o desenvolvimento
        val base64Data = "iVBORw0KGgoAAAANSUhEUgAAAhIAAAISAQAAAACxRhsSAAAEyUlEQVR4nO2dTa7bOgyFD58NZKgAXUCWYm+tS+oO7KVkB/YwgAK+gUiZzr29LdBEdYOjQRBL9gcLICjxR7Qo/rTN//0xAiCDDDLIIIMMMsggg4yNIdZ6yLiKAGsPYBWRcRUROQOlbz4D1iciMj75Pcgg4ydNVVUxqKrq0imQVHVKGapLpzolVQyaUfq2ge2x6ShzIeP9Gatrx+Fq/2RMGTLiLq5F+/CciPSPjGe8Bxlk/BZjvmSoXk+q388AgE5Vrz0wLJ3KCEDGFu9BBhmh9Q/XCkBl+AEI0r0HVgGALgPrXXQeHx940nuQQcZXzcUuKYAVEABQ4CaK9aQyTED5AbosSEu5L4YHjjIXMt6eMYub9WV5X09ljXfLvweGBcBw7SEj7sXcf/57kEHGp63o00076nyGyV/5lzKAlIuO9b59sPUocyHjfRnml0JSBWCOKG/Zb0qqGBbARzN0AkC/FBmNGIgyWXXnVEeD11TNdWqPLaCcktGK4X7+pTM//7AALqzmzjfdWaMAugDANnqUuZDxvgxf98uS72pzQqfbD4ayAzApDnEryikZbRh40Jg6AXXzWfenLqJbfNUFmHJKRgtG1acoguli2+luu2q7gk1EbbtKOSWjCSPkoWwWvenJFM16U6XFmALtfTKaMqodBURTP5lt5alSe2/UsnmoKKdktGCEhb7oyf2lC7DWbUDdn6ZMfUpGK0bw85voLXCLam/+F6+VOwfM8qecktGEEeyozX/qNpP/mwDzn25OVK77ZDRkuE7crCdUtemrvRtOnccD1LcBXPfJaMOI9j6A4ESN/tNUTa19o5yS0Ybh8f0Fwf0UDKe6If1wC/38ZDRnzJcMP1F6L+dNLYy/9gDSTWRMqjrBRqO2PdhcyHhfxnDtYQrUjSmRswelbC9wF2A9WV+R52e/BxlkfMEQuahiFhFgFQke/9nT9st50/lc+1aeNyWjGSPkoRRXUwnt1w2pNU+aKvvTYGpxf0pGC8bH/FPVT3z60XVqEuseKsopGa9nhHx+L3bivtLizgfgp1Isi38rlEI5JaMRI+RJu1L1kc0HZT85OLG47pPRnqGqt3C2FEBX66HUtp60lEAbH9z+x5oLGe/IiHXQpnK5M5es5lk9d1I1q2WpUJ+S0YJR86U8aW8CEIL3VWx39fris5RTMl7PiPlSE4DHoKgdhO7C0RTLTamNckrGyxm7cydVbWqwqOqB/apZdYnuVMopGa9nBMXoLlE3pqI+fTgNnXnuhIy/wSih0HQTbB6qae2B+eJ9ZbQGTwHG98loxqjrvh85KRUnAC+AAjzaUd6Y10dGM0Y0iHRvQtUcf0+ailWlwrOUUzJezihJT76Cd1mBu+gstXv9louLH+iyDJNCSh+sgO9x5kLG2zPMZvL0vXqc3y2qDJ3STcqetfY9/z3IIONLRv3eCVKGjKmm9+/yT8uA5Z+ePChwtLmQ8f4M/X7uFLP0VsEHST3SX2S3VjtPN37njIxWjE8+X2Jl0Mv3ToYffQZWgWI9A/Ml9wC6LPN58ernR5kLGe/L+PA9vsErnW0GfkhGGXwgBE9p75PxckasK7nl7tuAFzqvzikTzKQ8d0JGU4bor+/5RZuPMhcyyCCDDDLIIIMMMv59xv8Y/goKHcsQ8AAAAABJRU5ErkJggg=="

        val bitmap = base64ToBitmap(base64Data)

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code PIX",
                modifier = Modifier
                    .size(250.dp)
                    .padding(16.dp)
            )
        } else {
            Log.e("CheckoutDebug", "Bitmap retornou nulo ou falha na conversão.")
            Text(
                text = "Erro ao carregar QR Code",
                color = MaterialTheme.colorScheme.error
            )
        }

        // Pix copia e cola
        Text(
            "Pix QRCode:",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.bodyMedium.fontSize)
        )

        val scrollState = rememberScrollState()

        OutlinedTextField(
            value = sale.pixStr,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp) // altura ainda menor
                .padding(vertical = 4.dp) // padding menor
                .horizontalScroll(scrollState),
            readOnly = true,
            maxLines = 2,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Start,
                fontSize = MaterialTheme.typography.bodySmall.fontSize // tamanho de fonte menor
            )
        )

        Button(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("pix", sale.pixStr)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Pix Copiado!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(top = 4.dp) // reduz espaço acima do botão
        ) {
            Text("Copiar", fontSize = MaterialTheme.typography.bodyMedium.fontSize)
        }

        Spacer(modifier = Modifier.height(8.dp)) // menor espaçamento abaixo

        Text(
            "Total: ${sale.formattedTotal}",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize)
        )

        Spacer(modifier = Modifier.height(16.dp)) // espaçamento reduzido abaixo do Total

        val scope = rememberCoroutineScope()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                scope.launch {
                    val success = cartViewModel.confirmSale(sale.uuid)
                    if (success) {
                        purchasesViewModel.addPurchase(sale.booksSales)
                        cartViewModel.clearCart() // limpa carrinho
                        navController.navigate("purchases") {
                            popUpTo("store")
                        }
                    } else {
                        Toast.makeText(context, "Erro ao confirmar pagamento!", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text("Confirmar Pagamento")
            }
            OutlinedButton(onClick = {
                scope.launch {
                    val success = cartViewModel.cancelSale(sale.uuid)
                    if (success) {
                        navController.popBackStack() // volta ao carrinho
                    } else {
                        Toast.makeText(context, "Erro ao cancelar pagamento!", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text("Cancelar")
            }
        }

    }
}
