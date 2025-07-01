package br.com.newlibrarybookstore

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.newlibrarybookstore.data.Book
import br.com.newlibrarybookstore.ui.screens.BookDetailsScreen
import br.com.newlibrarybookstore.ui.screens.BookStoreScreen
import br.com.newlibrarybookstore.ui.screens.CartScreen
import br.com.newlibrarybookstore.ui.screens.PurchasesScreen
import br.com.newlibrarybookstore.ui.theme.NewLibraryBookStoreTheme
import br.com.newlibrarybookstore.ui.viewmodel.BookListViewModel
import br.com.newlibrarybookstore.ui.viewmodel.CartViewModel
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    // Instancia os ViewModels no nível da Activity para que sejam compartilhados
    private val bookListViewModel: BookListViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewLibraryBookStoreTheme {
                BookApp(bookListViewModel, cartViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookApp(bookListViewModel: BookListViewModel, cartViewModel: CartViewModel) {
    val navController = rememberNavController()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val context = LocalContext.current
    val totalQuantity by cartViewModel.totalQuantity.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookstore") },
                actions = {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        BadgedBox(
                            badge = {
                                if (totalQuantity > 0) {
                                    Badge { Text(cartItems.size.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Carrinho"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Defina os itens da sua barra de navegação
                val bottomNavItems = listOf(
                    "store" to Icons.Default.Storefront,
                    "purchases" to Icons.Default.History
                )

                bottomNavItems.forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen) },
                        label = { Text(screen.replaceFirstChar { it.uppercase() }) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen } == true,

                        // SUBSTITUA O SEU ONCLICK POR ESTE BLOCO:
                        onClick = {
                            navController.navigate(screen) {
                                // Este comando limpa a pilha de navegação até a tela inicial
                                popUpTo(navController.graph.findStartDestination().id)

                                // Este comando evita criar uma nova tela da loja se você já estiver nela
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = "store",
            Modifier.padding(innerPadding)
        ) {
            composable("store") {
                BookStoreScreen(
                    bookListViewModel = bookListViewModel,
                    cartViewModel = cartViewModel,
                    onBookClick = { book ->
                        // 1. Converte o objeto Book para uma string JSON
                        val bookJson = Gson().toJson(book)
                        // 2. Navega para a rota de detalhes, passando o JSON como argumento.
                        // O replace é uma segurança para evitar que caracteres como '/' quebrem a URL.
                        navController.navigate("book_details/${bookJson.replace('/', '|')}")
                    }
                )
            }

            // ROTA DE DETALHES CORRIGIDA
            composable(
                route = "book_details/{bookJson}",
                arguments = listOf(navArgument("bookJson") { type = NavType.StringType })
            ) { backStackEntry ->
                // 3. Pega a string JSON dos argumentos da rota e desfaz o replace.
                val bookJson = backStackEntry.arguments?.getString("bookJson")?.replace('|', '/')
                if (bookJson != null) {
                    // 4. Converte a string JSON de volta para um objeto Book
                    val book = Gson().fromJson(bookJson, Book::class.java)
                    // 5. Passa o objeto Book e o NavController para a tela de detalhes
                    BookDetailsScreen(
                        navController = navController,
                        book = book,
                        cartViewModel = cartViewModel
                    )
                } else {
                    // Tela de fallback caso algo dê muito errado
                    Text("Erro ao carregar detalhes do livro.")
                }
            }
            composable("purchases") { PurchasesScreen(cartViewModel) }
            composable("cart") {
                CartScreen(cartViewModel,
                    onCheckout = {
                        cartViewModel.checkout()

                    // Ação pós-checkout
                    Toast.makeText(context, "Compra finalizada com sucesso!", Toast.LENGTH_SHORT).show()
                    navController.navigate("purchases") {
                        popUpTo("store") // Limpa a pilha de navegação até a loja
                    }
                }
                )
            }
        }
    }
}