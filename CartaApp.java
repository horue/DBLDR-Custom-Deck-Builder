import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jnafilechooser.api.JnaFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CartaApp extends JFrame {

    private JPanel panel;
    private JPanel sidePanel;
    private List<Carta> cartas;
    private JLabel contadorLabel;
    private JLabel enlargedImageLabel;
    private JLabel fileNameLabel;
    private Image hexImage;
    private Image plusImage;
    private Image minusImage;


    public CartaApp() {
        setTitle("Deck Builder 1.7.1");
        setSize(1528, 865); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cartas = new ArrayList<>();

        // Carregar as imagens e redimensioná-las
        try {
            hexImage = ImageIO.read(new File("Visual\\hex.png"));
            plusImage = ImageIO.read(new File("Visual\\plus.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH);  // Botão menor
            minusImage = ImageIO.read(new File("Visual\\minus.png")).getScaledInstance(25, 25, Image.SCALE_SMOOTH);  // Botão menor
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Barra de ferramentas
        JToolBar toolBar = new JToolBar();
        JButton limparButton = new JButton("Limpar");
        limparButton.addActionListener(e -> limparCartas());
        toolBar.add(limparButton);
        add(toolBar, BorderLayout.NORTH);

        JButton handTestButton = new JButton("Hand Test");
        handTestButton.addActionListener(e -> realizarHandTest());
        toolBar.add(handTestButton);

        JButton salvarButton = new JButton("Salvar Deck");
        salvarButton.addActionListener(e -> salvarDecklist());
        toolBar.add(salvarButton);

        JButton carregarButton = new JButton("Carregar Deck");
        carregarButton.addActionListener(e -> carregarDecklist());
        toolBar.add(carregarButton);

        JButton exportToTTSButton = new JButton("Export to TTS");
        exportToTTSButton.addActionListener(e -> exportToTTS());
        toolBar.add(exportToTTSButton);

        JButton converterButton = new JButton("Convert to TTS"); // Novo botão
        converterButton.addActionListener(e -> converterCSVParaTSDB()); // Adicione um ActionListener
        toolBar.add(converterButton);

        add(toolBar, BorderLayout.NORTH);
        

        // Painel lateral
        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(300, getHeight()));  // Aumentei o tamanho da barra lateral

        // Adicionando espaçamento
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        enlargedImageLabel = new JLabel();
        fileNameLabel = new JLabel("Nome do Arquivo");
        fileNameLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Aumenta o tamanho da fonte do nome do arquivo
        contadorLabel = new JLabel("0 cards");
        contadorLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Aumenta o tamanho da fonte

        // Adicionando à barra lateral
        sidePanel.add(contadorLabel);
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10)); // Configurar o separador
        sidePanel.add(separator);
        sidePanel.add(fileNameLabel);
        sidePanel.add(enlargedImageLabel);

        // Divisória
        JSeparator separator2 = new JSeparator();
        separator2.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(separator2);

        // Área para mostrar os nomes e quantidades das cartas
        JPanel cardInfoPanel = new JPanel();
        cardInfoPanel.setLayout(new BoxLayout(cardInfoPanel, BoxLayout.Y_AXIS));
        sidePanel.add(cardInfoPanel);

        add(sidePanel, BorderLayout.WEST);


        
        // Painel principal de cartas
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int xPos = 10;
                int yPos = 10;
                int maxWidth = getWidth() - 50;

                panel.removeAll(); // Remove botões antigos

                int cardsPerRow = 8; // Máximo de cartas por fileira

                for (int i = 0; i < cartas.size(); i++) {
                    Carta carta = cartas.get(i);
                    Dimension newSize = getScaledDimension(new Dimension(carta.getImagem().getWidth(null), carta.getImagem().getHeight(null)), new Dimension(120, 180));
                    Image imagem = carta.getImagem().getScaledInstance(newSize.width, newSize.height, Image.SCALE_SMOOTH);
                    g.drawImage(imagem, xPos, yPos, this);

                    // Desenha o hexágono no centro da parte inferior da carta
                    int hexSize = 40;
                    int hexX = xPos + newSize.width / 2 - hexSize / 2;
                    int hexY = yPos + newSize.height - 40;  // Fica mais abaixo da carta
                    g.drawImage(hexImage, hexX, hexY, hexSize, hexSize, this);

                    // Desenha o número no hexágono
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 16));
                    g.drawString(carta.getNumero(), hexX + 12, hexY + 25);

                    // Distância dos botões de "+" e "-"
                    int buttonOffset = 5;

                    // Botão "+"
                    JLabel plusButton = new JLabel(new ImageIcon(plusImage));
                    plusButton.setBounds(hexX + hexSize + buttonOffset, hexY + (hexSize / 2) - 12, 25, 25);  // Botões menores
                    plusButton.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int numero = Integer.parseInt(carta.getNumero());
                            carta.setNumero(String.valueOf(numero + 1));
                            updateContador();
                            updateCardInfoPanel();
                            repaint();
                        }
                    });
                    panel.add(plusButton);

                    // Botão "-"
                    JLabel minusButton = new JLabel(new ImageIcon(minusImage));
                    minusButton.setBounds(hexX - buttonOffset - 25, hexY + (hexSize / 2) - 12, 25, 25);  // Botões menores
                    minusButton.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int numero = Integer.parseInt(carta.getNumero());
                            if (numero > 0) {
                                carta.setNumero(String.valueOf(numero - 1));
                                updateContador();
                                updateCardInfoPanel();
                                repaint();
                            }
                        }
                    });
                    panel.add(minusButton);

                    carta.setBounds(new Rectangle(xPos, yPos, newSize.width, newSize.height));

                    xPos += newSize.width + 10; // Diminuir distância horizontal entre as cartas
                    if ((i + 1) % cardsPerRow == 0) {
                        xPos = 10;
                        yPos += newSize.height + 10; // Diminuir distância vertical entre as cartas
                    }
                }
            }
        };

        panel.setLayout(null);

        

        // Configurar Drag and Drop
        panel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    // Obtém a lista de arquivos arrastados
                    java.util.List<File> files = (java.util.List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    for (File file : files) {
                        // Verifica se o arquivo é uma imagem PNG ou JPG
                        if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")) {
                            // Nome da carta (sem extensão)
                            String nomeCarta = file.getName().replace(".png", "").replace(".jpg", "");

                            // Caminho completo da imagem
                            String caminhoImagem = file.getAbsolutePath();

                            // Carregar a imagem e criar a nova carta
                            Carta novaCarta = new Carta(ImageIO.read(file), "1", nomeCarta, caminhoImagem);

                            // Adicionar a carta à lista
                            cartas.add(novaCarta);
                        }
                    }

                    // Atualiza o contador e o painel de informações da carta
                    updateContador();
                    updateCardInfoPanel();

                    // Repaint para refletir as mudanças na interface
                    repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        add(panel, BorderLayout.CENTER);

        // Clique para remover ou ampliar a carta
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Carta carta : cartas) {
                    Rectangle bounds = carta.getBounds();
                    if (bounds.contains(e.getPoint())) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            cartas.remove(carta);
                            updateContador();
                            updateCardInfoPanel();
                            repaint();
                            break;
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            mostrarCartaAmpliada(carta);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void updateContador() {
        // Somando todos os valores dos hexágonos das cartas
        int total = 0;
        for (Carta carta : cartas) {
            total += Integer.parseInt(carta.getNumero());  // Somando o número da carta (hexágono)
        }
        // Atualiza o label do contador com a soma total
        if (total > 1) {
            contadorLabel.setText("Total: " + total + " cards");
        }
        else
            contadorLabel.setText("Total: " + total + " card");
    }
    
    private void updateCardInfoPanel() {
        JPanel cardInfoPanel = (JPanel) sidePanel.getComponent(5);  // Acesso ao painel onde vamos mostrar os nomes e quantidades das cartas
        cardInfoPanel.removeAll();  // Limpa as informações antigas
    
        for (Carta carta : cartas) {
            JLabel label = new JLabel(carta.getNomeArquivo() + ": " + carta.getNumero());
            cardInfoPanel.add(label);
        }
    
        cardInfoPanel.revalidate();
        cardInfoPanel.repaint();
    }
    
    private void limparCartas() {
        cartas.clear();  // Limpar a lista de cartas
        updateContador();  // Atualiza o contador
        updateCardInfoPanel();  // Atualiza o painel de informações das cartas
        enlargedImageLabel.setIcon(null);
        fileNameLabel.setText("Nome do Arquivo");
        repaint();
    }

private void realizarHandTest() {
    if (cartas.size() < 13) {
        JOptionPane.showMessageDialog(this, "Você precisa de pelo menos 13 cartas no deck para realizar o Hand Test.", "Erro", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Cria um Map para armazenar a quantidade de cada carta
    Map<String, Integer> quantidadeCartas = new HashMap<>();

    // Preenche o Map com a quantidade de cada carta
    for (Carta carta : cartas) {
        // Aqui, estamos assumindo que "carta.getNumero()" seja único para a carta
        quantidadeCartas.put(carta.getNumero(), quantidadeCartas.getOrDefault(carta.getNumero(), 0) + 1);
    }

    // Cria uma lista para o deck com base nas quantidades
    List<Carta> deck = new ArrayList<>();

    // Preenche o deck com as cartas repetidas conforme a quantidade
    for (Carta carta : cartas) {
        int quantidade = quantidadeCartas.get(carta.getNumero());  // Obtém a quantidade de cartas dessa numeração
        for (int i = 0; i < quantidade; i++) {
            deck.add(carta); // Adiciona a carta repetidamente
        }
    }

    // Embaralha o deck
    Collections.shuffle(deck);

    // Cria listas para a mão e para os prêmios
    List<Carta> mao = new ArrayList<>();
    List<Carta> premios = new ArrayList<>();

    // Sorteia 7 cartas para a mão
    for (int i = 0; i < 7; i++) {
        Carta carta = deck.remove(0); // Remove a carta sorteada
        mao.add(carta);
    }

    // Sorteia 6 cartas para os prêmios
    for (int i = 0; i < 6; i++) {
        Carta carta = deck.remove(0); // Remove a carta sorteada
        premios.add(carta);
    }

    // Mostra as cartas sorteadas
    mostrarCartas(mao, premios);
}
    // Alterar a lógica de sortearCarta para garantir que ela esteja considerando a quantidade real de cartas
    private Carta sortearCarta(List<Carta> deck) {
        // Aqui você pode ajustar para que, ao sortear, a carta seja removida uma vez, mas a quantidade real dela seja levada em conta
        Random rand = new Random();
        return deck.get(rand.nextInt(deck.size())); // Sorteia aleatoriamente uma carta do deck
    }
        
    private void mostrarCartas(List<Carta> mao, List<Carta> premios) {
        JFrame handTestFrame = new JFrame("Hand Test");
        handTestFrame.setSize(800, 500);  // Aumenta a altura para acomodar o botão Retry
        handTestFrame.setLocationRelativeTo(null);
    
        // Painel principal que conterá as cartas e o botão de retry
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
    
        // Painel de cartas
        JPanel panelHandTest = new JPanel();
        panelHandTest.setLayout(new GridLayout(2, 7, 10, 10));
    
        // Adiciona cartas à mão
        for (Carta carta : mao) {
            JLabel label = new JLabel(new ImageIcon(carta.getImagem().getScaledInstance(100, 150, Image.SCALE_SMOOTH)));
            panelHandTest.add(label);
        }
    
        // Adiciona cartas aos prêmios
        for (Carta carta : premios) {
            JLabel label = new JLabel(new ImageIcon(carta.getImagem().getScaledInstance(100, 150, Image.SCALE_SMOOTH)));
            panelHandTest.add(label);
        }
    
        // Painel para o botão Retry
        JPanel buttonPanel = new JPanel();
        JButton retryButton = new JButton("Retry");
        retryButton.addActionListener(e -> {
        // Cria um Map para armazenar a quantidade de cada carta
        Map<String, Integer> quantidadeCartas = new HashMap<>();

        // Preenche o Map com a quantidade de cada carta no deck
        for (Carta carta : cartas) {
            quantidadeCartas.put(carta.getNumero(), quantidadeCartas.getOrDefault(carta.getNumero(), 0) + 1);
        }

        // Cria uma lista para o deck (com base nas quantidades de cada carta)
        List<Carta> deck = new ArrayList<>();
        for (Carta carta : cartas) {
            int quantidade = quantidadeCartas.get(carta.getNumero());  // Obtém a quantidade de cartas dessa numeração
            for (int i = 0; i < quantidade; i++) {
                deck.add(carta); // Adiciona a carta repetidamente
            }
        }

        // Embaralha o deck
        Collections.shuffle(deck);

        // Cria listas para a mão e para os prêmios
        List<Carta> novaMao = new ArrayList<>();
        List<Carta> novosPremios = new ArrayList<>();

        // Sorteia 7 cartas para a mão
        for (int i = 0; i < 7; i++) {
            Carta carta = deck.remove(0); // Remove a carta sorteada
            novaMao.add(carta);
        }

        // Sorteia 6 cartas para os prêmios
        for (int i = 0; i < 6; i++) {
            Carta carta = deck.remove(0); // Remove a carta sorteada
            novosPremios.add(carta);
        }

        // Atualiza o painel de cartas
        atualizarPainelCartas(panelHandTest, novaMao, novosPremios);
    });
        buttonPanel.add(retryButton);
    
        // Adiciona os painéis ao painel principal
        mainPanel.add(panelHandTest, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        handTestFrame.add(mainPanel);
        handTestFrame.setVisible(true);
    }


      private void salvarDecklist() {
        // Criando o JnaFileChooser para abrir a janela de "Salvar Como"
        JnaFileChooser jnaFileChooser = new JnaFileChooser();
        
        // Configura a janela para "Salvar Como"

        // Adiciona um filtro para arquivos .dbldr
        jnaFileChooser.setTitle("Save Decklist");
        jnaFileChooser.addFilter(".dbldr", ".dbldr");
        
        // Exibe a janela de "Salvar Como"
        boolean save = jnaFileChooser.showSaveDialog(this);
        
        if (save) {
            // Obtém o arquivo selecionado
            File file = jnaFileChooser.getSelectedFile();
            
            // Adiciona a extensão .dbldr se não estiver presente
            if (!file.getName().toLowerCase().endsWith(".dbldr")) {
                file = new File(file.getAbsolutePath() + ".dbldr");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Escreve as cartas no arquivo
                for (Carta carta : cartas) {
                    writer.write(carta.getNomeArquivo() + "," + carta.getNumero() + "," + carta.getImagemCaminhoCompleto());
                    writer.newLine();
                }
                
                // Mensagem de sucesso
                JOptionPane.showMessageDialog(this, "Decklist salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                // Mensagem de erro
                JOptionPane.showMessageDialog(this, "Erro ao salvar a decklist.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void carregarDecklist() {
        // Criando o JnaFileChooser para abrir a janela de "Abrir"
        JnaFileChooser jnaFileChooser = new JnaFileChooser();
    
    
        // Exibe a janela de "Abrir"
        boolean open = jnaFileChooser.showOpenDialog(this);
    
        if (open) {
            // Obtém o arquivo selecionado
            File file = jnaFileChooser.getSelectedFile();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                cartas.clear();
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String nomeArquivo = parts[0];
                        String quantidade = parts[1];
                        String caminhoImagem = parts[2];  // Agora estamos pegando o caminho da imagem
                    
                        // Usando o caminho da imagem para carregar a imagem corretamente
                        File imageFile = new File(caminhoImagem);
                        if (imageFile.exists()) {
                            Image imagem = ImageIO.read(imageFile);
                            Carta carta = new Carta(imagem, quantidade, nomeArquivo, caminhoImagem);
                            cartas.add(carta);
                        }
                    }
                }
                updateContador();
                updateCardInfoPanel();
                repaint();
                JOptionPane.showMessageDialog(this, "Decklist carregada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao carregar a decklist.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToTTS() {
        // Cria o JnaFileChooser para salvar o arquivo .dbldr
        JnaFileChooser jnaFileChooser = new JnaFileChooser();
        jnaFileChooser.setTitle("Save Decklist");
        jnaFileChooser.addFilter(".dbldr", ".dbldr");
    
        // Exibe a janela de "Salvar Como"
        boolean save = jnaFileChooser.showSaveDialog(this);
    
        if (save) {
            // Obtém o arquivo .dbldr selecionado
            File dbldrFile = jnaFileChooser.getSelectedFile();
    
            // Adiciona a extensão .dbldr se não estiver presente
            if (!dbldrFile.getName().toLowerCase().endsWith(".dbldr")) {
                dbldrFile = new File(dbldrFile.getAbsolutePath() + ".dbldr");
            }
    
            // Salva o arquivo .dbldr
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbldrFile))) {
                // Escreve as cartas no arquivo
                for (Carta carta : cartas) {
                    writer.write(carta.getNomeArquivo() + "," + carta.getNumero() + "," + carta.getImagemCaminhoCompleto());
                    writer.newLine();
                }
    
                // Mensagem de sucesso
                JOptionPane.showMessageDialog(this, "Decklist salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                // Mensagem de erro
                JOptionPane.showMessageDialog(this, "Erro ao salvar a decklist.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Define o arquivo TSDB com a mesma base de nome
            File tsdbFile = new File(dbldrFile.getAbsolutePath().replace(".dbldr", ".tsdb"));
    
            // Salva o arquivo TSDB
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(tsdbFile))) {
                // Escrever variáveis de configuração iniciais
                bw.write("cardsx=10");
                bw.newLine();
                bw.write("cardsy=6");
                bw.newLine();
                bw.write("card-width=745");
                bw.newLine();
                bw.write("card-height=1040");
                bw.newLine();
                bw.write("zoom=0.1");
                bw.newLine();
                bw.write("background-color=-16777216");
                bw.newLine();
    
                int maxCols = 10;
                int maxRows = 6;
                int currentCol = 0;
                int currentRow = 0;
    
                outerLoop:
                for (Carta carta : cartas) {
                    int numero = Integer.parseInt(carta.getNumero()); // Converte a string para inteiro
                    for (int i = 0; i < numero; i++) {
                        if (currentCol >= maxCols) {
                            currentCol = 0;
                            currentRow++;
                        }
                        if (currentRow >= maxRows) {
                            break outerLoop;
                        }
                        bw.write(currentCol + "_" + currentRow + "=" + carta.getImagemCaminhoCompleto().replace("\\", "\\\\"));
                        bw.newLine();
                        currentCol++;
                    }
                }
    
                // Completar o restante com null até 9_5
                while (currentRow < maxRows) {
                    if (currentCol >= maxCols) {
                        currentCol = 0;
                        currentRow++;
                    }
                    if (currentRow < maxRows) {
                        bw.write(currentCol + "_" + currentRow + "=null");
                        bw.newLine();
                        currentCol++;
                    }
                }
    
                // Mensagem de sucesso
                JOptionPane.showMessageDialog(this, "TSDB salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                // Mensagem de erro
                JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo TSDB.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void converterCSVParaTSDB() {
        // Cria o JnaFileChooser para abrir o arquivo CSV
        JnaFileChooser jnaFileChooser = new JnaFileChooser();
        jnaFileChooser.setTitle("Selecionar Arquivo CSV");
        jnaFileChooser.addFilter("Deckbuilder", "dbldr");
    
        // Exibe a janela de seleção de arquivo
        boolean open = jnaFileChooser.showOpenDialog(this);
    
        if (open) {
            // Obtém o arquivo CSV selecionado
            File csvFile = jnaFileChooser.getSelectedFile();
    
            // Cria o JnaFileChooser para salvar o arquivo TSDB
            JnaFileChooser saveFileChooser = new JnaFileChooser();
            saveFileChooser.setTitle("Salvar Arquivo TSDB");
            saveFileChooser.addFilter(".tsdb", ".tsdb");
    
            // Exibe a janela de "Salvar Como"
            boolean save = saveFileChooser.showSaveDialog(this);
    
            if (save) {
                // Obtém o arquivo TSDB selecionado para salvar
                File tsdbFile = saveFileChooser.getSelectedFile();
    
                // Adiciona a extensão .tsdb se não estiver presente
                if (!tsdbFile.getName().toLowerCase().endsWith(".tsdb")) {
                    tsdbFile = new File(tsdbFile.getAbsolutePath() + ".tsdb");
                }
    
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
                     BufferedWriter bw = new BufferedWriter(new FileWriter(tsdbFile))) {
    
                    // Escrever variáveis de configuração iniciais
                    bw.write("cardsx=10");
                    bw.newLine();
                    bw.write("cardsy=6");
                    bw.newLine();
                    bw.write("card-width=745");
                    bw.newLine();
                    bw.write("card-height=1040");
                    bw.newLine();
                    bw.write("zoom=0.1");
                    bw.newLine();
                    bw.write("background-color=-16777216");
                    bw.newLine();
    
                    String line;
                    int index = 0;
                    int maxCols = 10;
                    int maxRows = 6;
                    int currentCol = 0;
                    int currentRow = 0;
    
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 3) {
                            String cardName = parts[0];
                            int quantity = Integer.parseInt(parts[1]);
                            String path = parts[2].replace("\\", "\\\\"); // Corrigir caminho
    
                            for (int i = 0; i < quantity; i++) {
                                if (currentCol >= maxCols) {
                                    currentCol = 0;
                                    currentRow++;
                                }
                                if (currentRow >= maxRows) {
                                    break;
                                }
                                bw.write(currentCol + "_" + currentRow + "=" + path);
                                bw.newLine();
                                currentCol++;
                                index++;
                            }
                        }
                    }
    
                    // Completar o restante com null até 9_5
                    while (currentRow < maxRows) {
                        if (currentCol >= maxCols) {
                            currentCol = 0;
                            currentRow++;
                        }
                        if (currentRow < maxRows) {
                            bw.write(currentCol + "_" + currentRow + "=null");
                            bw.newLine();
                            currentCol++;
                        }
                    }
    
                    // Mensagem de sucesso
                    JOptionPane.showMessageDialog(this, "Conversão concluída: " + tsdbFile.getAbsolutePath(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Mensagem de erro
                    JOptionPane.showMessageDialog(this, "Erro ao converter o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    
    
    private void atualizarPainelCartas(JPanel panelHandTest, List<Carta> mao, List<Carta> premios) {
        panelHandTest.removeAll(); // Remove todos os componentes atuais
    
        // Adiciona as novas cartas à mão
        for (Carta carta : mao) {
            JLabel label = new JLabel(new ImageIcon(carta.getImagem().getScaledInstance(100, 150, Image.SCALE_SMOOTH)));
            panelHandTest.add(label);
        }
    
        // Adiciona as novas cartas aos prêmios
        for (Carta carta : premios) {
            JLabel label = new JLabel(new ImageIcon(carta.getImagem().getScaledInstance(100, 150, Image.SCALE_SMOOTH)));
            panelHandTest.add(label);
        }
    
        panelHandTest.revalidate(); // Revalida o painel para aplicar as mudanças
        panelHandTest.repaint();    // Repaint para garantir que as mudanças sejam exibidas
    }
    

    private void mostrarCartaAmpliada(Carta carta) {
        Dimension newSize = getScaledDimension(new Dimension(carta.getImagem().getWidth(null), carta.getImagem().getHeight(null)), new Dimension(250, 350));
        Image imagem = carta.getImagem().getScaledInstance(newSize.width, newSize.height, Image.SCALE_SMOOTH);
        enlargedImageLabel.setIcon(new ImageIcon(imagem));
        fileNameLabel.setText(carta.getNomeArquivo());
    }

    private class Carta {
        private Image imagem;
        private String numero;
        private String nomeArquivo;
        private String caminhoImagem;
        private Rectangle bounds;
    
        public Carta(Image imagem, String numero, String nomeArquivo, String caminhoImagem) {
            this.imagem = imagem;
            this.numero = numero;
            this.nomeArquivo = nomeArquivo;
            this.caminhoImagem = caminhoImagem;  // Armazena o caminho da imagem
        }
    
        public Image getImagem() {
            return imagem;
        }
    
        public String getNumero() {
            return numero;
        }
    
        public void setNumero(String numero) {
            this.numero = numero;
        }
    
        public String getNomeArquivo() {
            return nomeArquivo;
        }

        public String getImagemCaminhoCompleto() {
            return caminhoImagem;  // Método para acessar o caminho completo da imagem
        }
    
        public Rectangle getBounds() {
            return bounds;
        }
    
        public void setBounds(Rectangle bounds) {
            this.bounds = bounds;
        }
    }
    

    // Método para manter a proporção da imagem
    private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // Verifica se precisamos redimensionar a largura
        if (original_width > bound_width) {
            new_width = bound_width;
            new_height = (new_width * original_height) / original_width;
        }

        // Verifica se precisamos redimensionar a altura
        if (new_height > bound_height) {
            new_height = bound_height;
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CartaApp().setVisible(true));
    }
}
