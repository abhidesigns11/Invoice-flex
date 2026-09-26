package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.BusinessProfile
import com.example.data.model.InvoiceType
import com.example.data.model.UserProfile
import com.example.ui.screens.AddEditCatalogItemDialog
import com.example.ui.screens.AddEditPartyDialog
import com.example.ui.screens.CreateEditInvoiceScreen
import com.example.ui.screens.CustomThemeBuilderScreen
import com.example.ui.screens.EWayIrnScreen
import com.example.ui.screens.ForYouScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HsnFinderScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.InvoiceDetailPreviewScreen
import com.example.ui.screens.InvoiceSettingsScreen
import com.example.ui.screens.InvoicesListScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.OnboardingWizardScreen
import com.example.ui.screens.PartiesScreen
import com.example.ui.screens.PartyDetailScreen
import com.example.ui.screens.PosBillingScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsProfileScreen
import com.example.ui.screens.SignupScreen
import com.example.ui.screens.ThemeColorScreen
import com.example.ui.screens.ssFabricationCategories
import com.example.ui.theme.CanvasBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryCobalt
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.BillingViewModel
import com.example.ui.viewmodel.InvoiceDraftItem
import com.example.ui.viewmodel.SessionStatus

sealed class AppScreen {
    object Home : AppScreen()
    object Inventory : AppScreen()
    object Invoices : AppScreen()
    object EWayIrn : AppScreen()
    object HsnFinder : AppScreen()
    object Parties : AppScreen()
    object Reports : AppScreen()
    object Settings : AppScreen()
    object PosBilling : AppScreen()
    object CreateInvoice : AppScreen()
    object InvoiceDetail : AppScreen()
    object PartyDetail : AppScreen()
    object ForYou : AppScreen()
    object More : AppScreen()
    object OnboardingWizard : AppScreen()
    object ThemeColor : AppScreen()
    object CustomThemeBuilder : AppScreen()
    object InvoiceSettings : AppScreen()
    data class LegalCompliance(val tab: com.example.ui.screens.LegalTab = com.example.ui.screens.LegalTab.PRIVACY) : AppScreen()
}

enum class MainNavTab(val title: String, val icon: ImageVector, val testTag: String) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "nav_tab_dashboard"),
    PARTIES("Parties", Icons.Default.People, "nav_tab_parties"),
    PRODUCTS("Items", Icons.Default.Inventory2, "nav_tab_products"),
    FOR_YOU("For You", Icons.Default.AutoAwesome, "nav_tab_for_you"),
    MORE("More", Icons.Default.Menu, "nav_tab_more")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AuthGate()
            }
        }
    }
}

/**
 * Top-level gate: decides whether to show Login/Sign up, the setup wizard,
 * or the real app, based on Firebase auth + whether the account has finished
 * the onboarding Q&A yet.
 */
@Composable
fun AuthGate(authViewModel: AuthViewModel = viewModel()) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    var showSignUp by remember { mutableStateOf(false) }
    var unauthLegalTab by remember { mutableStateOf<com.example.ui.screens.LegalTab?>(null) }

    if (unauthLegalTab != null) {
        BackHandler { unauthLegalTab = null }
        com.example.ui.screens.LegalComplianceScreen(
            initialTab = unauthLegalTab!!,
            onNavigateBack = { unauthLegalTab = null }
        )
        return
    }

    when (authState.sessionStatus) {
        SessionStatus.CHECKING -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CanvasBg),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryCobalt)
            }
        }

        SessionStatus.LOGGED_OUT -> {
            if (showSignUp) {
                SignupScreen(
                    uiState = authState,
                    onSignUp = { email, password, confirm ->
                        authViewModel.signUp(email, password, confirm)
                    },
                    onNavigateToLogin = {
                        authViewModel.clearMessages()
                        showSignUp = false
                    },
                    onOpenLegal = { tab -> unauthLegalTab = tab }
                )
            } else {
                LoginScreen(
                    uiState = authState,
                    onLogin = { email, password -> authViewModel.login(email, password) },
                    onForgotPassword = { email -> authViewModel.sendPasswordReset(email) },
                    onNavigateToSignUp = {
                        authViewModel.clearMessages()
                        showSignUp = true
                    },
                    onMessageShown = { authViewModel.clearMessages() },
                    onOpenLegal = { tab -> unauthLegalTab = tab }
                )
            }
        }

        SessionStatus.NEEDS_SETUP -> {
            val billingViewModel: BillingViewModel = viewModel()
            val billingState by billingViewModel.uiState.collectAsStateWithLifecycle()
            OnboardingWizardScreen(
                state = billingState,
                viewModel = billingViewModel,
                authViewModel = authViewModel,
                onFinish = { /* sessionStatus flips to READY automatically */ }
            )
        }

        SessionStatus.READY -> {
            InvoiceFlexApp(
                authViewModel = authViewModel,
                onLogout = { authViewModel.logout() }
            )
        }
    }
}

private fun mapToBusinessProfile(profile: UserProfile): BusinessProfile = BusinessProfile(
    id = 1,
    businessName = profile.companyName.ifBlank { "My Business" },
    phone = profile.phone,
    email = profile.email,
    address = profile.address,
    city = profile.city,
    state = profile.state,
    pincode = profile.pincode,
    gstin = profile.gstin,
    upiId = profile.upiId,
    bankName = profile.bankName,
    accountHolderName = profile.accountHolderName,
    accountNumber = profile.accountNumber,
    ifscCode = profile.ifscCode,
    branch = profile.branch,
    isGstEnabled = profile.isGstEnabled
)

@Composable
fun InvoiceFlexApp(
    viewModel: BillingViewModel = viewModel(),
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Keep the local invoice/report data (still on-device for now) in sync with
    // whatever this account's Firestore profile currently says.
    LaunchedEffect(authState.profile) {
        authState.profile?.let { profile ->
            viewModel.saveBusinessProfile(mapToBusinessProfile(profile))
        }
    }

    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
    var selectedNavTab by remember { mutableStateOf(MainNavTab.DASHBOARD) }

    var showQuickAddPartyDialog by remember { mutableStateOf(false) }
    var showQuickAddItemDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val isSubScreen = currentScreen !in listOf(
        AppScreen.Home,
        AppScreen.Parties,
        AppScreen.Inventory,
        AppScreen.ForYou,
        AppScreen.More
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (!isSubScreen) {
                NavigationBar(
                    tonalElevation = 4.dp,
                    containerColor = PureWhite
                ) {
                    MainNavTab.values().forEach { tab ->
                        val isSelected = selectedNavTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                selectedNavTab = tab
                                currentScreen = when (tab) {
                                    MainNavTab.DASHBOARD -> AppScreen.Home
                                    MainNavTab.PARTIES -> AppScreen.Parties
                                    MainNavTab.PRODUCTS -> AppScreen.Inventory
                                    MainNavTab.FOR_YOU -> AppScreen.ForYou
                                    MainNavTab.MORE -> AppScreen.More
                                }
                            },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryCobalt,
                                selectedTextColor = PrimaryCobalt,
                                indicatorColor = PrimaryFixed,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                is AppScreen.Home -> {
                    HomeScreen(
                        state = state,
                        onNavigateToCreateInvoice = { type ->
                            viewModel.initNewInvoiceDraft(type)
                            currentScreen = AppScreen.CreateInvoice
                        },
                        onNavigateToInvoiceDetail = { invId ->
                            viewModel.selectInvoiceById(invId)
                            currentScreen = AppScreen.InvoiceDetail
                        },
                        onNavigateToInvoices = {
                            currentScreen = AppScreen.Invoices
                        },
                        onNavigateToParties = {
                            selectedNavTab = MainNavTab.PARTIES
                            currentScreen = AppScreen.Parties
                        },
                        onNavigateToInventory = {
                            selectedNavTab = MainNavTab.PRODUCTS
                            currentScreen = AppScreen.Inventory
                        },
                        onNavigateToPos = {
                            currentScreen = AppScreen.PosBilling
                        },
                        onNavigateToReports = {
                            currentScreen = AppScreen.Reports
                        },
                        onNavigateToSettings = {
                            currentScreen = AppScreen.Settings
                        },
                        onNavigateToHsnFinder = {
                            currentScreen = AppScreen.HsnFinder
                        },
                        onAddPartyClick = { showQuickAddPartyDialog = true },
                        onAddItemClick = { showQuickAddItemDialog = true },
                        onNavigateToForYou = {
                            selectedNavTab = MainNavTab.FOR_YOU
                            currentScreen = AppScreen.ForYou
                        },
                        onNavigateToThemeColor = {
                            currentScreen = AppScreen.ThemeColor
                        },
                        onNavigateToSetupWizard = {
                            currentScreen = AppScreen.OnboardingWizard
                        },
                        onNavigateToEWayIrn = {
                            currentScreen = AppScreen.EWayIrn
                        },
                        viewModel = viewModel,
                        onNavigateToPartyDetail = { partyId ->
                            viewModel.selectPartyById(partyId)
                            currentScreen = AppScreen.PartyDetail
                        }
                    )
                }

                is AppScreen.Parties -> {
                    BackHandler {
                        selectedNavTab = MainNavTab.DASHBOARD
                        currentScreen = AppScreen.Home
                    }
                    PartiesScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigateToPartyDetail = { partyId ->
                            viewModel.selectPartyById(partyId)
                            currentScreen = AppScreen.PartyDetail
                        },
                        onNavigateToSettings = {
                            currentScreen = AppScreen.Settings
                        },
                        onNavigateToInvoiceDetail = { invId ->
                            viewModel.selectInvoiceById(invId)
                            currentScreen = AppScreen.InvoiceDetail
                        }
                    )
                }

                is AppScreen.Inventory -> {
                    BackHandler {
                        selectedNavTab = MainNavTab.DASHBOARD
                        currentScreen = AppScreen.Home
                    }
                    InventoryScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigateBack = {
                            selectedNavTab = MainNavTab.DASHBOARD
                            currentScreen = AppScreen.Home
                        },
                        onNavigateToHsnFinder = {
                            currentScreen = AppScreen.HsnFinder
                        }
                    )
                }

                is AppScreen.ForYou -> {
                    BackHandler {
                        selectedNavTab = MainNavTab.DASHBOARD
                        currentScreen = AppScreen.Home
                    }
                    ForYouScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigateToHsnFinder = {
                            currentScreen = AppScreen.HsnFinder
                        }
                    )
                }

                is AppScreen.More -> {
                    BackHandler {
                        selectedNavTab = MainNavTab.DASHBOARD
                        currentScreen = AppScreen.Home
                    }
                    MoreScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigateToInvoiceSettings = {
                            currentScreen = AppScreen.InvoiceSettings
                        },
                        onNavigateToAccountSettings = {
                            currentScreen = AppScreen.Settings
                        },
                        onNavigateToHsnFinder = {
                            currentScreen = AppScreen.HsnFinder
                        },
                        onNavigateToSetupWizard = {
                            currentScreen = AppScreen.OnboardingWizard
                        },
                        onNavigateToLegal = { tab ->
                            currentScreen = AppScreen.LegalCompliance(tab)
                        },
                        onLogout = onLogout
                    )
                }

                is AppScreen.InvoiceSettings -> {
                    BackHandler {
                        currentScreen = AppScreen.More
                    }
                    InvoiceSettingsScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = AppScreen.More },
                        onNavigateToThemeColor = { currentScreen = AppScreen.ThemeColor },
                        onNavigateToCustomThemeBuilder = { currentScreen = AppScreen.CustomThemeBuilder },
                        onNavigateToEWayIrn = { currentScreen = AppScreen.EWayIrn }
                    )
                }

                is AppScreen.ThemeColor -> {
                    BackHandler {
                        currentScreen = AppScreen.InvoiceSettings
                    }
                    ThemeColorScreen(
                        onNavigateBack = { currentScreen = AppScreen.InvoiceSettings },
                        onThemeSaved = { themeName, colorHex ->
                            // Update active theme styling
                        },
                        onNavigateToCustomTheme = {
                            currentScreen = AppScreen.CustomThemeBuilder
                        }
                    )
                }

                is AppScreen.CustomThemeBuilder -> {
                    BackHandler {
                        currentScreen = AppScreen.ThemeColor
                    }
                    CustomThemeBuilderScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = AppScreen.ThemeColor }
                    )
                }

                is AppScreen.OnboardingWizard -> {
                    BackHandler {
                        currentScreen = AppScreen.Home
                    }
                    OnboardingWizardScreen(
                        state = state,
                        viewModel = viewModel,
                        authViewModel = authViewModel,
                        onFinish = { currentScreen = AppScreen.Home }
                    )
                }

                is AppScreen.Invoices -> {
                    BackHandler {
                        selectedNavTab = MainNavTab.DASHBOARD
                        currentScreen = AppScreen.Home
                    }
                    InvoicesListScreen(
                        state = state,
                        onNavigateToCreateInvoice = { type ->
                            viewModel.initNewInvoiceDraft(type)
                            currentScreen = AppScreen.CreateInvoice
                        },
                        onNavigateToInvoiceDetail = { invId ->
                            viewModel.selectInvoiceById(invId)
                            currentScreen = AppScreen.InvoiceDetail
                        }
                    )
                }

                is AppScreen.EWayIrn -> {
                    BackHandler {
                        selectedNavTab = MainNavTab.DASHBOARD
                        currentScreen = AppScreen.Home
                    }
                    EWayIrnScreen(
                        onNavigateBack = {
                            selectedNavTab = MainNavTab.DASHBOARD
                            currentScreen = AppScreen.Home
                        },
                        onNavigateToInvoiceDetail = { invId ->
                            viewModel.selectInvoiceById(invId)
                            currentScreen = AppScreen.InvoiceDetail
                        }
                    )
                }

                is AppScreen.HsnFinder -> {
                    BackHandler {
                        selectedNavTab = MainNavTab.DASHBOARD
                        currentScreen = AppScreen.Home
                    }
                    HsnFinderScreen(
                        onNavigateBack = {
                            selectedNavTab = MainNavTab.DASHBOARD
                            currentScreen = AppScreen.Home
                        },
                        onSelectHsnForNewItem = { hsnRec ->
                            viewModel.initNewInvoiceDraft(InvoiceType.SALE_INVOICE)
                            viewModel.addDraftCustomItem(
                                InvoiceDraftItem(
                                    itemName = hsnRec.description.take(40),
                                    hsnCode = hsnRec.code,
                                    quantity = 1.0,
                                    unit = if (hsnRec.type == "Goods") "Pcs" else "Hour",
                                    unitPrice = 25000.0,
                                    taxRate = hsnRec.gstRate
                                )
                            )
                            currentScreen = AppScreen.CreateInvoice
                        }
                    )
                }

                is AppScreen.CreateInvoice -> {
                    BackHandler {
                        currentScreen = AppScreen.Home
                    }
                    CreateEditInvoiceScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = AppScreen.Home },
                        onInvoiceSaved = { newId ->
                            currentScreen = AppScreen.InvoiceDetail
                        }
                    )
                }

                is AppScreen.InvoiceDetail -> {
                    BackHandler {
                        currentScreen = AppScreen.Home
                    }
                    InvoiceDetailPreviewScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigateBack = {
                            currentScreen = AppScreen.Home
                        }
                    )
                }

                is AppScreen.PosBilling -> {
                    BackHandler {
                        currentScreen = AppScreen.Home
                    }
                    PosBillingScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = AppScreen.Home },
                        onCheckoutSuccess = { newId ->
                            currentScreen = AppScreen.InvoiceDetail
                        }
                    )
                }

                is AppScreen.PartyDetail -> {
                    BackHandler {
                        currentScreen = AppScreen.Parties
                    }
                    PartyDetailScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = AppScreen.Parties },
                        onNavigateToInvoiceDetail = { invId ->
                            viewModel.selectInvoiceById(invId)
                            currentScreen = AppScreen.InvoiceDetail
                        },
                        onNavigateToSettings = {
                            currentScreen = AppScreen.Settings
                        }
                    )
                }

                is AppScreen.Reports -> {
                    BackHandler {
                        currentScreen = AppScreen.Home
                    }
                    ReportsScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }

                is AppScreen.Settings -> {
                    BackHandler {
                        currentScreen = AppScreen.Home
                    }
                    SettingsProfileScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigateBack = {
                            currentScreen = AppScreen.Home
                        },
                        onNavigateToHsnFinder = {
                            currentScreen = AppScreen.HsnFinder
                        },
                        onNavigateToReports = {
                            currentScreen = AppScreen.Reports
                        },
                        onNavigateToPos = {
                            currentScreen = AppScreen.PosBilling
                        }
                    )
                }

                is AppScreen.LegalCompliance -> {
                    val complianceTab = (currentScreen as AppScreen.LegalCompliance).tab
                    BackHandler {
                        currentScreen = AppScreen.More
                    }
                    com.example.ui.screens.LegalComplianceScreen(
                        initialTab = complianceTab,
                        onNavigateBack = { currentScreen = AppScreen.More }
                    )
                }
            }
        }

        if (showQuickAddPartyDialog) {
            AddEditPartyDialog(
                defaultType = com.example.data.model.PartyType.CUSTOMER,
                onDismiss = { showQuickAddPartyDialog = false },
                onSave = { newParty ->
                    viewModel.saveParty(newParty)
                    showQuickAddPartyDialog = false
                }
            )
        }

        if (showQuickAddItemDialog) {
            AddEditCatalogItemDialog(
                initial = null,
                categories = ssFabricationCategories,
                onDismiss = { showQuickAddItemDialog = false },
                onSave = { newItem ->
                    viewModel.saveItem(newItem)
                    showQuickAddItemDialog = false
                }
            )
        }
    }
}
