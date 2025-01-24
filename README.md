# Chargepoint App

An Android application for locating and managing electric vehicle charging points in West Yorkshire. This app allows users to view, search, and manage charging points through both list and map views.

## Features

- User authentication (login/register)
- Interactive map view of charging points
- Searchable list view of charging points
- Real-time charging point status updates
- Location-based services
- Admin interface for managing charging points

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern and uses modern Android development practices.

### Core Components

#### Activities

1. **MainActivity**
   - Entry point of the application
   - Handles navigation to login/register screens
   - Checks authentication state

2. **DashboardActivity**
   - Main container activity after login
   - Manages ViewPager2 for switching between list and map views
   - Handles location permissions
   - Implements loading state management

3. **LoginActivity**
   - Handles user authentication
   - Validates user credentials
   - Manages Firebase Authentication

4. **RegisterActivity**
   - Handles new user registration
   - Validates registration data
   - Creates new Firebase Authentication accounts

#### Fragments

1. **ChargepointListFragment**
   - Displays searchable list of charging points
   - Implements filtering and sorting functionality
   - Handles CRUD operations for charging points

2. **ChargepointMapFragment**
   - Displays charging points on Google Maps
   - Handles map initialization and marker placement
   - Manages location services and permissions
   - Implements marker clustering for better performance

#### ViewModels

1. **ChargepointViewModel**
   - Manages charging point data
   - Handles database operations
   - Provides data to UI components
   - Implements data transformation and filtering

#### Data Classes

1. **Chargepoint**
   - Model class for charging point data
   - Contains location, status, and type information
   - Implements Room Entity for database storage

#### Database

1. **AppDatabase**
   - Room database implementation
   - Manages local data persistence
   - Handles database migrations
   - Implements singleton pattern

2. **DatabaseInitializer**
   - Handles initial database setup
   - Populates default data
   - Manages background initialization

#### Adapters

1. **ChargepointAdapter**
   - RecyclerView adapter for charging point list
   - Handles item click events
   - Implements filtering and sorting

2. **DashboardPagerAdapter**
   - ViewPager2 adapter for main dashboard
   - Manages fragment lifecycle
   - Handles tab switching

#### Authentication

1. **FirebaseAuthHelper**
   - Wrapper for Firebase Authentication
   - Manages user sessions
   - Handles login/logout operations
   - Implements callback pattern for auth state

#### Utilities

1. **CsvImporter**
   - Handles CSV file import
   - Parses charging point data
   - Validates import format

## UML Diagrams

### Use Case Diagram

```mermaid
graph TD
    subgraph Users
        U[Regular User]
        A[Admin User]
    end

    subgraph Authentication
        L[Login]
        R[Register]
        LO[Logout]
    end

    subgraph Chargepoint Management
        VM[View Map]
        VL[View List]
        S[Search Chargepoints]
        F[Filter Chargepoints]
        AC[Add Chargepoint]
        EC[Edit Chargepoint]
        DC[Delete Chargepoint]
        I[Import CSV]
    end

    subgraph Location Services
        GL[Get Location]
        NP[Navigate to Point]
    end

    U --> L
    U --> R
    U --> LO
    U --> VM
    U --> VL
    U --> S
    U --> F
    U --> GL
    U --> NP

    A --> L
    A --> LO
    A --> AC
    A --> EC
    A --> DC
    A --> I
    A --> VM
    A --> VL
    A --> S
    A --> F
```

### Class Diagram

```mermaid
classDiagram
    class MainActivity {
        -FirebaseAuthHelper authHelper
        +onCreate()
        -initializeViews()
        -setupClickListeners()
    }

    class DashboardActivity {
        -ViewPager2 viewPager
        -TabLayout tabLayout
        -ChargepointViewModel viewModel
        +onCreate()
        -setupViewPager()
        -checkLocationPermission()
    }

    class ChargepointViewModel {
        -ChargepointRepository repository
        -LiveData~List~Chargepoint~~ chargepoints
        +getAllChargepoints()
        +insert(Chargepoint)
        +update(Chargepoint)
        +delete(Chargepoint)
    }

    class Chargepoint {
        -int id
        -String name
        -double latitude
        -double longitude
        -String chargerType
        -String status
        +getters()
        +setters()
    }

    class AppDatabase {
        -static AppDatabase instance
        -ChargepointDao chargepointDao
        +getInstance()
        +chargepointDao()
    }

    class FirebaseAuthHelper {
        -FirebaseAuth auth
        -AuthCallback callback
        +login()
        +register()
        +logout()
    }

    class ChargepointMapFragment {  
        -GoogleMap map
        -ChargepointViewModel viewModel
        +onMapReady()
        -updateMarkers()
    }

    class ChargepointListFragment {
        -RecyclerView recyclerView
        -ChargepointAdapter adapter
        -ChargepointViewModel viewModel
        +onCreateView()
        -setupRecyclerView()
    }

    MainActivity --> FirebaseAuthHelper
    DashboardActivity --> ChargepointViewModel
    ChargepointViewModel --> AppDatabase
    ChargepointMapFragment --> ChargepointViewModel
    ChargepointListFragment --> ChargepointViewModel
    AppDatabase --> Chargepoint
```

### Data Flow Diagram

```mermaid
graph TD
    subgraph User Interface
        UI1[Login Screen]
        UI2[Dashboard]
        UI3[Map View]
        UI4[List View]
    end

    subgraph View Models
        VM1[ChargepointViewModel]
    end

    subgraph Repositories
        R1[ChargepointRepository]
    end

    subgraph Local Storage
        DB1[Room Database]
    end

    subgraph External Services
        ES1[Firebase Auth]
        ES2[Google Maps]
    end

    UI1 -->|Auth Data| ES1
    ES1 -->|Auth Token| UI2
    UI2 -->|User Input| VM1
    UI3 -->|Location Data| ES2
    UI4 -->|Filter/Search| VM1
    VM1 -->|CRUD Operations| R1
    R1 -->|Read/Write| DB1
    DB1 -->|Data| R1
    R1 -->|Data| VM1
    VM1 -->|Updates| UI3
    VM1 -->|Updates| UI4
    ES2 -->|Map Data| UI3
```

## Dependencies

- AndroidX Core and AppCompat
- Google Maps Android SDK
- Firebase Authentication
- Room Database
- ViewPager2
- Material Design Components
- RecyclerView
- LiveData and ViewModel

## Setup

1. Clone the repository
2. Add your Google Maps API key to `local.properties`:
   ```
   MAPS_API_KEY=your_api_key_here
   ```
3. Configure Firebase:
   - Add your `google-services.json` to the app directory
   - Enable Email/Password authentication in Firebase Console

## Building and Running

1. Open the project in Android Studio
2. Sync project with Gradle files
3. Build the project
4. Run on an emulator or physical device

## Architecture Diagram

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│     Views       │     │   ViewModels     │     │     Models      │
│  (Activities &  │◄────┤                  │◄────┤                 │
│   Fragments)    │     │                  │     │                 │
└─────────────────┘     └──────────────────┘     └─────────────────┘
         ▲                       ▲                        ▲
         │                       │                        │
         │                       │                        │
         │               ┌──────────────────┐            │
         └───────────────┤  Repositories    ├────────────┘
                         │                  │
                         └──────────────────┘
                                 ▲
                                 │
                    ┌──────────────────────┐
                    │    Local Database    │
                    │      (Room DB)       │
                    └──────────────────────┘
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
