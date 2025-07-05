package br.com.newlibrarybookstore

import android.os.Bundle
import android.util.Log
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
import br.com.newlibrarybookstore.ui.screens.CheckoutScreen
import br.com.newlibrarybookstore.ui.screens.PurchasesScreen
import br.com.newlibrarybookstore.ui.theme.NewLibraryBookStoreTheme
import br.com.newlibrarybookstore.ui.viewmodel.BookListViewModel
import br.com.newlibrarybookstore.ui.viewmodel.CartViewModel
import br.com.newlibrarybookstore.ui.viewmodel.PurchasesViewModel
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val bookListViewModel: BookListViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private val purchasesViewModel: PurchasesViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewLibraryBookStoreTheme {
                BookApp(bookListViewModel, cartViewModel, purchasesViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookApp(bookListViewModel: BookListViewModel, cartViewModel: CartViewModel, purchasesViewModel: PurchasesViewModel) {
    val navController = rememberNavController()
    val cartItems by cartViewModel.cartItems.collectAsState()
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

                val bottomNavItems = listOf(
                    "store" to Icons.Default.Storefront,
                    "purchases" to Icons.Default.History
                )

                bottomNavItems.forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen) },
                        label = { Text(screen.replaceFirstChar { it.uppercase() }) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen } == true,

                        onClick = {
                            navController.navigate(screen) {
                                popUpTo(navController.graph.findStartDestination().id)

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
                    navController = navController,
                    onBookClick = { book ->
                        val bookJson = Gson().toJson(book)
                        navController.navigate("book_details/${bookJson.replace('/', '|')}")
                    }
                )
            }

            composable(
                route = "book_details/{bookJson}",
                arguments = listOf(navArgument("bookJson") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookJson = backStackEntry.arguments?.getString("bookJson")?.replace('|', '/')
                if (bookJson != null) {
                    val book = Gson().fromJson(bookJson, Book::class.java)
                    BookDetailsScreen(
                        navController = navController,
                        book = book,
                        cartViewModel = cartViewModel
                    )
                } else {
                    Text("Erro ao carregar detalhes do livro.")
                }
            }
            composable("purchases") {
                Log.d("onMainActivityCheckoutDebug", "PurchasesViewModel: $purchasesViewModel")
                PurchasesScreen(purchasesViewModel) }
            composable("cart") { CartScreen(navController, cartViewModel) }
            composable("checkout") {
                CheckoutScreen(navController, cartViewModel, purchasesViewModel)
            }

        }
    }
}