# KPT Design System (Compose Multiplatform)

## 🚀 New Architecture & Extensibility (2024)

- **Design Tokens**: Centralized via `KptDesignSystemProvider` and `CompositionLocal` for spacing,
  radii, elevation, etc. (see `config/DesignTokens.kt`).
- **Theming**: Use `KptTheme` for light, dark, and AMOLED support. All tokens are injected for
  consistent UI.
- **Component Naming**: All custom components use the `Kpt` prefix (e.g., `KptButton`,
  `KptFloatingActionButton`).
- **Slot API**: All major components use slot APIs for maximum flexibility and composability.
- **Accessibility & Testing**: All components support `testTag` and `contentDescription` for UI
  testing and screen readers.
- **Migration**: `CMPTheme` → `KptTheme`, `CMPButton` → `KptButton` (deprecated wrappers provided),
  new `KptFloatingActionButton`.
- **Extensible**: Easily override tokens or theme at the app level. Designed for standalone,
  multiplatform use.

---

# MP Design System Library

The CMP Design System is a robust, theme-aware UI component library built with Jetpack Compose for
Kotlin Multiplatform (KMP). It provides a consistent, accessible, and customizable set of components
tailored for modern application development, leveraging Material 3 principles and a flexible theming
system.

- Components
    - CMPAlertDialog
    - CMPBottomAppBar
    - CMPBottomSheet
    - CMPButton
    - CMPCard
    - CMPProgressIndicator
    - CMPScaffold
    - CMPTextField
    - CMPTopAppBar
- Theming
- Preferences and ViewModel
- Adaptive Layouts

## CMPAlertDialog

A customizable dialog with support for both Material3 AlertDialog and BasicAlertDialog.

### Properties

| Property          | Required | Optinal | Variants | Description                                                      |
|-------------------|----------|---------|----------|------------------------------------------------------------------|
| onDismissRequest  | ✅        |         | All      | Callback for dialog dismissal.                                   |
| confirmButton     | ✅        |         | CUSTOM   | Composable for the confirm button.                               |
| modifier          |          | ✅       | All      | Modifier for the dialog. Default: `Modifier`.                    |
| dismissButton     |          | ✅       | CUSTOM   | Optional dismiss button.                                         |
| icon              |          | ✅       | CUSTOM   | Optional icon above title.                                       |
| title             |          | ✅       | CUSTOM   | Optional title composable.                                       |
| text              |          | ✅       | CUSTOM   | Optional body text.                                              |
| shape             |          | ✅       | CUSTOM   | Dialog shape. Default: `AlertDialogDefaults.shape`.              |
| containerColor    |          | ✅       | CUSTOM   | Background color. Default: `AlertDialogDefaults.containerColor`. |
| iconContentColor  |          | ✅       | CUSTOM   | Icon color. Default: `AlertDialogDefaults.iconContentColor`.     |
| titleContentColor |          | ✅       | CUSTOM   | Title color. Default: `AlertDialogDefaults.titleContentColor`.   |
| textContentColor  |          | ✅       | CUSTOM   | Text color. Default: `AlertDialogDefaults.textContentColor`.     |
| tonalElevation    |          | ✅       | CUSTOM   | Elevation. Default: `AlertDialogDefaults.TonalElevation`.        |
| properties        |          | ✅       | All      | Platform-specific properties. Default: `DialogProperties()`.     |
| variant           |          | ✅       | All      | Dialog style. Default: `AlertDialogVariant.CUSTOM`.              |
| basicContent      | ✅        |         | BASIC    | Custom content for `BASIC` variant.                              |

### Example

``` yaml
@Composable
fun DialogExample() {
    var open by remember { mutableStateOf(true) }
    if (open) {
        CMPAlertDialog(
            onDismissRequest = { open = false },
            confirmButton = { Button(onClick = { open = false }) { Text("OK") } },
            title = { Text("Welcome") },
            text = { Text("This is a custom dialog.") },
            variant = AlertDialogVariant.CUSTOM
        )
    }
}
```

## CMPBottomAppBar

A bottom navigation bar with support for actions or custom content.

### Properties

| Property             | Required | Optional | Variants            | Description                                                       |
|----------------------|----------|----------|---------------------|-------------------------------------------------------------------|
| actions              | ✅        |          | BOTTOM_WITH_ACTIONS | Composable actions for the bar.                                   |
| modifier             |          | ✅        | All                 | Modifier. Default: `Modifier`.                                    |
| floatingActionButton |          | ✅        | BOTTOM_WITH_ACTIONS | Optional FAB.                                                     |
| containerColor       |          | ✅        | All                 | Background color. Default: `BottomAppBarDefaults.containerColor`. |
| contentColor         |          | ✅        | All                 | Content color. Default: `contentColorFor(containerColor)`.        |
| tonalElevation       |          | ✅        | All                 | Elevation. Default: `BottomAppBarDefaults.ContainerElevation`.    |
| contentPadding       |          | ✅        | All                 | Padding. Default: `BottomAppBarDefaults.ContentPadding`.          |
| windowInsets         |          | ✅        | All                 | Insets. Default: `BottomAppBarDefaults.windowInsets`.             |
| scrollBehavior       |          | ✅        | All                 | Scroll behavior.                                                  |
| variant              |          | ✅        | All                 | Style. Default: `BottomAppBarVariant.BOTTOM_WITH_ACTIONS`.        |
| customContent        | ✅        |          | BOTTOM_CUSTOM       | Custom content for `BOTTOM_CUSTOM`.                               |

### Example

```yaml
@Composable
  fun BottomBarExample() {
  CMPBottomAppBar(
  actions = {
  IconButton(onClick = {}) { Icon(Icons.Default.Home, "Home") }
  IconButton(onClick = {}) { Icon(Icons.Default.Settings, "Settings") }
  },
  floatingActionButton = { FloatingActionButton(onClick = {}) { Icon(Icons.Default.Add, "Add") } }
  )
}
```

## CMPBottomSheet

A modal bottom sheet for temporary content display.

### Properties

| Property            | Required | Optional | Variants | Description                                                       |
|---------------------|----------|----------|----------|-------------------------------------------------------------------|
| onDismiss           | ✅        |          | All      | Callback when sheet is dismissed.                                 |
| modifier            |          | ✅        | All      | Modifier. Default: `Modifier`.                                    |
| sheetState          |          | ✅        | All      | Sheet state. Default: `rememberModalBottomSheetState()`.          |
| sheetMaxWidth       |          | ✅        | All      | Max width. Default: `BottomSheetDefaults.SheetMaxWidth`.          |
| shape               |          | ✅        | All      | Shape. Default: `BottomSheetDefaults.ExpandedShape`.              |
| containerColor      |          | ✅        | All      | Background. Default: `BottomSheetDefaults.ContainerColor`.        |
| contentColor        |          | ✅        | All      | Content color. Default: `contentColorFor(containerColor)`.        |
| tonalElevation      |          | ✅        | All      | Elevation. Default: `0.dp`.                                       |
| scrimColor          |          | ✅        | All      | Scrim color. Default: `BottomSheetDefaults.ScrimColor`.           |
| dragHandle          |          | ✅        | All      | Drag handle. Default: `BottomSheetDefaults.DragHandle()`.         |
| contentWindowInsets |          | ✅        | All      | Insets. Default: `BottomSheetDefaults.windowInsets`.              |
| properties          |          | ✅        | All      | Sheet properties. Default: `ModalBottomSheetDefaults.properties`. |
| sheetContent        | ✅        |          | All      | Content with a `hideSheet` callback.                              |

### Example

```yaml
@Composable
  fun BottomSheetExample() {
  var open by remember { mutableStateOf(true) }
  if (open) {
  CMPBottomSheet(
  onDismiss = { open = false },
  sheetContent = { hideSheet ->
  Column {
  Text("Bottom Sheet Content")
  Button(onClick = hideSheet) { Text("Close") }
  }
  }
  )
  }
}
```

## CMPButton

A versatile button supporting multiple Material3 button types.

### Properties

| Property          | Required | Optional | Variants | Description                                    |
|-------------------|----------|----------|----------|------------------------------------------------|
| onClick           | ✅        |          | All      | Click callback.                                |
| modifier          |          | ✅        | All      | Modifier. Default: `Modifier`.                 |
| enabled           |          | ✅        | All      | Enabled state. Default: `true`.                |
| variant           |          | ✅        | All      | Button style. Default: `ButtonVariant.FILLED`. |
| colors            |          | ✅        | All      | Custom colors.                                 |
| elevation         |          | ✅        | All      | Elevation for supported variants.              |
| border            |          | ✅        | All      | Border stroke.                                 |
| shape             |          | ✅        | All      | Shape. Default: variant-specific.              |
| interactionSource |          | ✅        | All      | Interaction source.                            |
| contentPadding    |          | ✅        | All      | Content padding. Default: variant-specific.    |
| content           | ✅        |          | All      | Button content.                                |

Example

```yaml
@Composable
  fun ButtonExample() {
  CMPButton(
  onClick = { /* Handle click */ },
  variant = ButtonVariant.FILLED,
  content = { Text("Click Me") }
  )
}
```

## CMPCard

A card component supporting filled, elevated, and outlined variants.

### Properties

| Property          | Required | Optional | Variants         | Description                                                                                    |
|-------------------|----------|----------|------------------|------------------------------------------------------------------------------------------------|
| modifier          |          | ✅        | All              | Modifier. Default: `Modifier`.                                                                 |
| onClick           |          | ✅        | All              | Click callback. Default: `{}`.                                                                 |
| enabled           |          | ✅        | All              | Enabled state. Default: `true`.                                                                |
| variant           |          | ✅        | All              | Card style. Default: `CardVariant.FILLED`.                                                     |
| shape             |          | ✅        | All              | Shape. Default: variant-specific.                                                              |
| colors            |          | ✅        | All              | Custom colors.                                                                                 |
| elevation         |          | ✅        | All              | Elevation. Default: variant-specific.                                                          |
| borderStroke      |          | ✅        | FILLED, OUTLINED | Border stroke. Default: `null` for `FILLED`, `CardDefaults.outlinedCardBorder` for `OUTLINED`. |
| interactionSource |          | ✅        | All              | Interaction source.                                                                            |
| content           | ✅        |          | All              | Card content.                                                                                  |

### Example

```yaml
@Composable
  fun CardExample() {
  CMPCard(
  variant = CardVariant.OUTLINED,
  content = {
  Column {
  Text("Card Title")
  Text("Card Content")
  }
  }
  )
}
```

## CMPProgressIndicator

A progress indicator for determinate and indeterminate modes.

### Properties

| Property                           | Required | Optional | Variants                                 | Description                                         |
|------------------------------------|----------|----------|------------------------------------------|-----------------------------------------------------|
| modifier                           |          | ✅        | All                                      | Modifier. Default: `Modifier`.                      |
| color                              |          | ✅        | All                                      | Progress color.                                     |
| trackColor                         |          | ✅        | All                                      | Track color.                                        |
| strokeCap                          |          | ✅        | All                                      | Stroke cap style.                                   |
| circularStrokeWidth                |          | ✅        | CIRCULAR                                 | Stroke width for circular variants.                 |
| gapSize                            |          | ✅        | All                                      | Gap between segments.                               |
| determinateLinearDrawStopIndicator |          | ✅        | DETERMINATE_LINEAR                       | Custom stop indicator for linear determinate.       |
| progress                           | ✅        |          | DETERMINATE_LINEAR, DETERMINATE_CIRCULAR | Progress value (0f to 1f). Default: `0f`.           |
| variant                            |          | ✅        | All                                      | Indicator style. Default: `INDETERMINATE_CIRCULAR`. |

### Example

```yaml
@Composable
  fun ProgressExample() {
  var progress by remember { mutableStateOf(0.5f) }
  CMPProgressIndicator(
  variant = ProgressIndicatorVariant.DETERMINATE_CIRCULAR,
  progress = progress
  )
}
```

## CMPScaffold

A layout scaffold with support for top/bottom bars, FAB, and pull-to-refresh.

### Properties

| Property                       | Required | Optional | Variants | Description                                                        |
|--------------------------------|----------|----------|----------|--------------------------------------------------------------------|
| modifier                       |          | ✅        | All      | Modifier. Default: `Modifier`.                                     |
| topBar                         |          | ✅        | All      | Top bar composable. Default: `{}`.                                 |
| bottomBar                      |          | ✅        | All      | Bottom bar composable. Default: `{}`.                              |
| snackbarHost                   |          | ✅        | All      | Snackbar host. Default: `{}`.                                      |
| floatingActionButton           |          | ✅        | All      | FAB. Default: `{}`.                                                |
| floatingActionButtonPosition   |          | ✅        | All      | FAB position. Default: `FabPosition.End`.                          |
| containerColor                 |          | ✅        | All      | Background color. Default: `MaterialTheme.colorScheme.background`. |
| contentColor                   |          | ✅        | All      | Content color. Default: `contentColorFor(containerColor)`.         |
| contentWindowInsets            |          | ✅        | All      | Insets. Default: `ScaffoldDefaults.contentWindowInsets`.           |
| rememberPullToRefreshStateData |          | ✅        | All      | Pull-to-refresh state.                                             |
| content                        | ✅        |          | All      | Main content with padding values.                                  |

### Example

```yaml
@Composable
  fun ScaffoldExample() {
  CMPScaffold(
  topBar = { CMPTopAppBar(title = { Text("App") }) },
  content = { padding -> Text("Content", Modifier.padding(padding)) }
  )
}
```

## CMPTextField

A text input field supporting filled and outlined styles.

### Properties

| Property             | Required | Optional | Variants | Description                                                |
|----------------------|----------|----------|----------|------------------------------------------------------------|
| value                | ✅        |          | All      | Current text value.                                        |
| onValueChange        | ✅        |          | All      | Callback for text changes.                                 |
| modifier             |          | ✅        | All      | Modifier. Default: `Modifier`.                             |
| enabled              |          | ✅        | All      | Enabled state. Default: `true`.                            |
| readOnly             |          | ✅        | All      | Read-only state. Default: `false`.                         |
| textStyle            |          | ✅        | All      | Text style. Default: `LocalTextStyle.current`.             |
| label                |          | ✅        | All      | Label.                                                     |
| placeholder          |          | ✅        | All      | Placeholder text.                                          |
| leadingIcon          |          | ✅        | All      | Leading icon.                                              |
| trailingIcon         |          | ✅        | All      | Trailing icon.                                             |
| prefix               |          | ✅        | All      | Prefix composable.                                         |
| suffix               |          | ✅        | All      | Suffix composable.                                         |
| supportingText       |          | ✅        | All      | Supporting text.                                           |
| isError              |          | ✅        | All      | Error state. Default: `false`.                             |
| visualTransformation |          | ✅        | All      | Text transformation. Default: `VisualTransformation.None`. |
| keyboardOptions      |          | ✅        | All      | Keyboard options. Default: `KeyboardOptions.Default`.      |
| keyboardActions      |          | ✅        | All      | Keyboard actions. Default: `KeyboardActions.Default`.      |
| singleLine           |          | ✅        | All      | Single-line mode. Default: `false`.                        |
| maxLines             |          | ✅        | All      | Max lines. Default: `1` or `Int.MAX_VALUE`.                |
| minLines             |          | ✅        | All      | Min lines. Default: `1`.                                   |
| interactionSource    |          | ✅        | All      | Interaction source.                                        |
| shape                |          | ✅        | All      | Shape. Default: variant-specific.                          |
| colors               |          | ✅        | All      | Colors. Default: variant-specific.                         |
| variant              |          | ✅        | All      | Style. Default: `TextFieldVariant.FILLED`.                 |

### Example

```yaml
@Composable
  fun TextFieldExample() {
  var text by remember { mutableStateOf("") }
  CMPTextField(
  value = text,
  onValueChange = { text = it },
  label = { Text("Name") },
  variant = TextFieldVariant.OUTLINED
  )
}
```

## CMPTopAppBar

A top app bar supporting small, center-aligned, medium, and large variants.

### Properties

| Property        | Required | Optional | Variants      | Description                                                          |
|-----------------|----------|----------|---------------|----------------------------------------------------------------------|
| title           | ✅        |          | All           | Title composable.                                                    |
| modifier        |          | ✅        | All           | Modifier. Default: `Modifier`.                                       |
| navigationIcon  |          | ✅        | All           | Leading icon. Default: `{}`.                                         |
| actions         |          | ✅        | All           | Trailing actions. Default: `{}`.                                     |
| expandedHeight  |          | ✅        | All           | Expanded height. Default: variant-specific.                          |
| collapsedHeight |          | ✅        | MEDIUM, LARGE | Collapsed height for scrollable variants. Default: variant-specific. |
| windowInsets    |          | ✅        | All           | Insets. Default: `TopAppBarDefaults.windowInsets`.                   |
| colors          |          | ✅        | All           | Colors. Default: variant-specific.                                   |
| scrollBehavior  |          | ✅        | All           | Scroll behavior. Default: variant-specific.                          |
| variant         |          | ✅        | All           | Style. Default: `TopAppBarVariant.SMALL`.                            |

### Example

```yaml
@Composable
  fun TopBarExample() {
  CMPTopAppBar(
  title = { Text("My App") },
  navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, "Back") } },
  actions = { IconButton(onClick = {}) { Icon(Icons.Default.Settings, "Settings") } }
  )
}
```

## Theming

The CMP design system supports dynamic theming through CMPTheme. To integrate with user preferences,
use the ThemePreferencesRepository and ThemeBaseViewmodel.

### Custom Color Palettes

Define two custom color palettes based on palette name:

```yaml
object Palette {
  val BluePalette = ColorPalette(
  primary = Color(0xFF0288D1),
  // Define other properties
  )
  val GreenPalette = ColorPalette(
  primary = Color(0xFF388E3C),
  // Define other properties
  )

fun getPalette(name:
  String): ColorPalette {
             return when (name) {
             "Blue" -> BluePalette
             "Green" -> GreenPalette
             else -> BluePalette // Default
  }
}
}
```

### Applying Theme with ViewModel

See the implementation section below for how to collect state and pass it to CMPTheme.

## Preferences and ViewModel

The `ThemePreferencesRepository` and `ThemeBaseViewmodel` manage theme preferences reactively.
And example if you
use [russhwolf/multiplatform-settings](https://github.com/russhwolf/multiplatform-settings)

### Implementation

**ThemePreferencesRepositoryImpl**

```yaml
class ThemePreferencesRepositoryImpl(
private val dataStore: UserPreferencesDataStore
): ThemePreferencesRepository {

  private val _themeData = MutableStateFlow(ThemeData("Blue"))
  override val themeData: Flow<ThemeData> = _themeData.asStateFlow()

  override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
    dataStore.putValue(DARK_THEME_CONFIG_KEY, darkThemeConfig.configName)
    refreshDarkThemeConfig()
}

  override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
    dataStore.putValue(DYNAMIC_COLOR_KEY, useDynamicColor)
    refreshDynamicColorPreference()
}

  override suspend fun setColorPaletteName(paletteName: String) {
    dataStore.putValue(THEME_BRAND_KEY, paletteName)
    refreshColorPaletteName()
}

  override suspend fun refreshDarkThemeConfig() {
  val configName = dataStore.getValue(
  DARK_THEME_CONFIG_KEY,
  DarkThemeConfig.FOLLOW_SYSTEM.configName
  )
  _themeData.value = _themeData.value.copy(
  darkThemeConfig = DarkThemeConfig.fromValue(configName)
  )
}

  override suspend fun refreshDynamicColorPreference() {
  val useDynamic = dataStore.getValue(DYNAMIC_COLOR_KEY, false)
  _themeData.value = _themeData.value.copy(useDynamicColor = useDynamic)
}

  override suspend fun refreshColorPaletteName() {
  val paletteName = dataStore.getValue(THEME_BRAND_KEY, "Blue")
  _themeData.value = _themeData.value.copy(colorPaletteName = paletteName)
}
}
```

***ThemeViewModel***

```yaml
class ThemeViewModel(
private val themeRepository: ThemePreferencesRepository
): ThemeBaseViewmodel() {

  override val themeUiState: StateFlow<ThemeData> = themeRepository.themeData.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = ThemeData("Blue")
  )

  override fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
    viewModelScope.launch {
    themeRepository.setDarkThemeConfig(darkThemeConfig)
    }
}

  override fun updateDynamicColorPreference(useDynamicColor: Boolean) {
    viewModelScope.launch {
    themeRepository.setDynamicColorPreference(useDynamicColor)
    }
}

  override fun updateColorPaletteName(paletteName: String) {
    viewModelScope.launch {
    themeRepository.setColorPaletteName(paletteName)
    }
}
}
```

### Using ThemeViewModel for Theme Changes

The `ThemeViewModel` (as implemented previously) is designed to manage theme preferences reactively,
allowing users to update the color palette, dark theme configuration, or dynamic color preference.
You can inject `ThemeViewModel` into any composable where theme changes are needed (e.g., a settings
screen) and call its methods to update the theme:

- `updateColorPaletteName(paletteName: String)`: Switches the color palette (e.g., "Blue" or "
  Green").
- `updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig)`: Changes the dark mode setting (e.g.,
  FOLLOW_SYSTEM, DARK, LIGHT).
- `updateDynamicColorPreference(useDynamicColor: Boolean)`: Toggles dynamic color support.
  These updates are propagated through the themeUiState flow, which the top-level composable can
  collect to apply to CMPTheme.

**Example: Settings Screen with ThemeViewModel**

```yaml
@Composable
fun SettingsScreen(viewModel: ThemeViewModel = viewModel()) {
  Column {
  Button(onClick = { viewModel.updateColorPaletteName("Green") }) {
  Text("Switch to Green Palette")
  }
  Button(onClick = { viewModel.updateDarkThemeConfig(DarkThemeConfig.DARK) }) {
  Text("Enable Dark Mode")
  }
  Button(onClick = { viewModel.updateDynamicColorPreference(true) }) {
  Text("Enable Dynamic Colors")
  }
  }
}
```

### AppViewModel Implementation

Your AppViewModel snippet suggests a structure that collects theme data from
ThemePreferencesRepository and maps it to an AppUiState.
AppUiState Definition

First, define a sealed interface for the app's UI state to handle loading and success states:

```yaml
sealed interface AppUiState {
data object Loading: AppUiState
data class Success(val themeData:
  ThemeData): AppUiState
}
```

Here's the `AppViewModel` that collects theme data from `ThemePreferencesRepository` and exposes it
as a `StateFlow<AppUiState>`:

```yaml
class AppViewModel(
private val themeRepository: ThemePreferencesRepository,
): ViewModel() {

  val uiState: StateFlow<AppUiState> = themeRepository.themeData
    .onStart {
    // Initialize theme preferences
    themeRepository.refreshColorPaletteName()
    themeRepository.refreshDarkThemeConfig()
    themeRepository.refreshDynamicColorPreference()
    }
    .map { themeData ->
    AppUiState.Success(themeData)
    }
    .stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = AppUiState.Loading,
    )
}
```

**Top-Level Composable**

```yaml
@Composable
fun App(appViewModel: AppViewModel = viewModel()) {
  val uiState by appViewModel.uiState.collectAsState()

  when (uiState) {
  is AppUiState.Loading -> {
  Box(
  modifier = Modifier.fillMaxSize(),
  contentAlignment = Alignment.Center
  ) {
  CircularProgressIndicator()
  }
  }
  is AppUiState.Success -> {
  val themeData = (uiState as AppUiState.Success).themeData
  val isDarkMode = when (themeData.darkThemeConfig) {
  DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
  DarkThemeConfig.DARK -> true
  DarkThemeConfig.LIGHT -> false
  }
  val palette = Palette.getPalette(themeData.colorPaletteName)

  CMPTheme(
  colorPalette = palette,
  isDarkMode = isDarkMode,
  dynamicColor = themeData.useDynamicColor
  ) {
  CMPScaffold(
  topBar = { CMPTopAppBar(title = { Text("My App") }) },
  content = { padding ->
  // Navigate to SettingsScreen or other content
  SettingsScreen() // Example: Include settings for theme changes
}
  )
}
}
}
}
```

## Adaptive Layouts

The CMP Design System includes adaptive layout scaffolds that automatically adjust to different
screen sizes and postures using Material3 adaptive layouts. These composables encapsulate navigation
and pane behaviors for list-detail and supporting pane experiences.

### AdaptiveNavigableListDetailPaneScaffold

A generic, adaptive list-detail scaffold supporting animated transitions, selection, and responsive
layout behavior. It uses ListDetailPaneScaffold internally and manages pane navigation
automatically.

### Properties

| Property                | Required | Optional | Description                                                                |
|-------------------------|----------|----------|----------------------------------------------------------------------------|
| items                   | ✅        |          | List of items to show in the list pane. Must implement `PaneScaffoldItem`. |
| listPaneItem            | ✅        |          | Composable for list item UI (inside selectable surface).                   |
| detailPaneContent       | ✅        |          | Composable to render selected item details.                                |
| modifier                |          | ✅        | Modifier for the scaffold. Default: `Modifier`.                            |
| extraPaneContent        |          | ✅        | Optional third pane composable.                                            |
| paneExpansionDragHandle |          | ✅        | Optional drag handle composable for resizing.                              |
| paneExpansionState      |          | ✅        | State holder for pane expansion.                                           |
| cardShape               |          | ✅        | Shape of the item card.                                                    |
| cardElevation           |          | ✅        | Elevation of the item card.                                                |
| cardColors              |          | ✅        | Custom card colors.                                                        |
| cardBorder              |          | ✅        | Custom card border stroke.                                                 |

### Example

```yaml
AdaptiveNavigableListDetailPaneScaffold(
items = myItems,
listPaneItem = { item, isListAndDetailVisible, isListVisible, sharedTransitionScope, visibilityScope ->
Text(text = item.title)
},
detailPaneContent = { item, isListAndDetailVisible, isDetailVisible, sharedTransitionScope, visibilityScope ->
Text(text = item.details)
}
)
```

**Notes**

- If you're using AdaptiveNavigableListDetailPaneScaffold, your item class must implement:

```yaml
interface PaneScaffoldItem<T> {
val id: T
}
```

### AdaptiveListDetailPaneScaffold

Simplified adaptive layout using ListDetailPaneScaffold, with external navigation via
ThreePaneScaffoldNavigator. Suitable when you need more control over navigation or want to plug in
your own navigator.

### Properties

| Property                | Required | Optional | Description                                                            |
|-------------------------|----------|----------|------------------------------------------------------------------------|
| mainPaneContent         | ✅        |          | Composable for list/main pane. Accepts a lambda to navigate to detail. |
| detailPaneContent       | ✅        |          | Composable for detail pane. Accepts a lambda to navigate back.         |
| modifier                |          | ✅        | Modifier for the scaffold. Default: `Modifier`.                        |
| navigator               |          | ✅        | Optional navigation controller. Defaults to internal state.            |
| extraPaneContent        |          | ✅        | Optional third pane composable.                                        |
| paneExpansionDragHandle |          | ✅        | Optional drag handle composable.                                       |
| paneExpansionState      |          | ✅        | Optional state controller for pane expansion.                          |

### Example

```yaml
AdaptiveListDetailPaneScaffold(
mainPaneContent = { navigateToDetail ->
Button(onClick = navigateToDetail) {
Text("Open Detail")
}
},
detailPaneContent = { navigateBack ->
Button(onClick = navigateBack) {
Text("Go Back")
}
}
)
```

### AdaptiveNavigableSupportingPaneScaffold

Scaffold for a two-pane layout with a main and supporting pane. Designed for workflows where users
select or trigger an action in the main pane that reveals additional UI in the supporting pane.

### Properties

| Property                | Required | Optional | Description                                                               |
|-------------------------|----------|----------|---------------------------------------------------------------------------|
| mainPaneContent         | ✅        |          | Composable content for the main pane. Receives a lambda to navigate.      |
| supportingPaneContent   | ✅        |          | Composable content for the supporting pane. Receives a lambda to go back. |
| modifier                |          | ✅        | Modifier for the scaffold. Default: `Modifier`.                           |
| scaffoldNavigator       |          | ✅        | Optional external navigator controller.                                   |
| extraPaneContent        |          | ✅        | Optional third pane composable.                                           |
| paneExpansionDragHandle |          | ✅        | Optional drag handle for resizing.                                        |
| paneExpansionState      |          | ✅        | Optional custom expansion state.                                          |

### Example

```yaml
AdaptiveNavigableSupportingPaneScaffold(
mainPaneContent = { navigateToSupporting ->
Button(onClick = navigateToSupporting) {
Text("Open Details")
}
},
supportingPaneContent = { navigateBack ->
Button(onClick = navigateBack) {
Text("Back")
}
}
)
```

### AdaptiveNavigationSuiteScaffold

A responsive scaffold that adapts its navigation UI (e.g., bottom bar, rail, drawer) based on the
current screen size and device posture.

It wraps NavigationSuiteScaffold and automatically selects the optimal layout using WindowSizeClass
and WindowAdaptiveInfo. This is ideal for apps targeting phones, tablets, and foldables using a
single composable.

### Properties

| Property              | Required | Optional | Description                                                                  |
|-----------------------|----------|----------|------------------------------------------------------------------------------|
| navigationSuiteItems  | ✅        |          | Defines destinations or actions using [NavigationSuiteScope].                |
| modifier              |          | ✅        | Modifier for the scaffold. Default: `Modifier`.                              |
| layoutType            |          | ✅        | Force a specific layout (e.g., Drawer, Rail, BottomBar).                     |
| navigationSuiteColors |          | ✅        | Navigation UI color config. Default: `NavigationSuiteDefaults.colors()`.     |
| containerColor        |          | ✅        | Background color. Default: `NavigationSuiteScaffoldDefaults.containerColor`. |
| contentColor          |          | ✅        | Content color. Default: `NavigationSuiteScaffoldDefaults.contentColor`.      |
| content               | ✅        |          | Main content displayed beside or below the navigation UI.                    |

### Example

```yaml
AdaptiveNavigationSuiteScaffold(
navigationSuiteItems = {
NavigationSuiteItem(
icon = { Icon(Icons.Default.Home, contentDescription = null) },
label = { Text("Home") },
selected = true,
onClick = { /* Navigate */ }
)
NavigationSuiteItem(
icon = { Icon(Icons.Default.Settings, contentDescription = null) },
label = { Text("Settings") },
selected = false,
onClick = { /* Navigate */ }
)
},
content = {
Text("Hello from Adaptive Navigation Scaffold!")
}
)
```