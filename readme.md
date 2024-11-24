# DBLDR - Custom Deck Builder (1.6.2)

Custom Deck Builder is a Java Swing application designed for creating and managing card decks. It features an intuitive graphical interface for adding, removing, and testing card decks. This readme provides an overview of the application's features and instructions on how to use it.

## Features

-   **Add Cards via Drag and Drop:** Drag and drop PNG or JPG images to add cards to your deck.
-   **Card Management:** Add or remove cards with a simple click.
-   **Deck Saving and Loading:** Save your deck to a file or load a deck from a file.
-   **Hand Test:** Simulate drawing a hand of 7 cards and 6 prize cards to test your deck.
-   **Dynamic Interface:** View card images, names, and quantities, all updated in real-time.

## Getting Started

### Prerequisites

-   Java Development Kit (JDK) 8 or higher
-   Java Swing and AWT libraries

### Installation (Cloning)

1.  Clone the repository or download the source code.
2.  Ensure that `hex.png`, `plus.png`, and `minus.png` images are in the root directory of the project.
3.  Compile and run the `CartaApp.java` file.

### Running the Application

1.  Open a terminal or command prompt.
2.  Navigate to the directory containing `CartaApp.java`.
3.  Compile the code using `javac CartaApp.java`.
4.  Run the application using `java CartaApp`.

### Installation (JAR File)

1.  Download the jar file from the releases tab.

### Running the Application

1.  Run the jar file.

## Usage

### Adding Cards

-   **Drag and Drop:** Drag PNG or JPG images onto the main panel to add cards. The card's name is derived from the image filename (excluding the extension).

### Managing Cards

-   **Increase Quantity:** Click the plus button (`+`) near the card to increase its quantity.
-   **Decrease Quantity:** Click the minus button (`-`) near the card to decrease its quantity. The quantity will not go below zero.
-   **Remove Card:** Right-click on a card to remove it from the deck.
-   **View Card:** Left-click on a card to view an enlarged version in the side panel.

### Saving and Loading Decks

-   **Save Deck:** Click on the "Salvar Deck" button to save your current deck to a `.dbldr` file (The .dbldr is a CSV structure). A file chooser dialog will appear for you to specify the file name and location.
-   **Load Deck:** Click on the "Carregar Deck" button to load a deck from a `.dbldr` file. A file chooser dialog will appear for you to select the file.

### Hand Test

-   **Perform Hand Test:** Click on the "Hand Test" button to simulate drawing a hand of 7 cards and 6 prize cards. A new window will display the drawn cards. (Not working properly yet.)
-   **Retry Hand Test:** In the hand test window, click the "Retry" button to perform another hand test with the same deck.

### Clearing the Deck

-   **Clear Deck:** Click the "Limpar" button to remove all cards from the deck.

## Code Overview

### Main Components

-   **CartaApp:** The main JFrame application.
-   **JToolBar:** Toolbar with buttons for clearing the deck, performing hand tests, saving, and loading decks.
-   **JPanel:** Main panel for displaying cards and side panel for additional information.
-   **TransferHandler:** Handles drag and drop functionality for adding cards.

### Card Class

A `Carta` object represents a card in the deck and includes:

-   Image
-   Quantity
-   Name
-   Full image path

### Helper Methods

-   **updateContador():** Updates the card count display.
-   **updateCardInfoPanel():** Updates the side panel with card names and quantities.
-   **limparCartas():** Clears all cards from the deck.
-   **realizarHandTest():** Performs a hand test by drawing 7 cards for the hand and 6 cards for prizes.
-   **sortearCarta():** Randomly selects a card from the deck.
-   **mostrarCartas():** Displays the hand and prize cards in a new window.
-   **salvarDecklist():** Saves the current deck to a file.
-   **carregarDecklist():** Loads a deck from a file.

## Screenshots
WIP.