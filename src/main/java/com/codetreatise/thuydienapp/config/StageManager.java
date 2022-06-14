package com.codetreatise.thuydienapp.config;

import static org.slf4j.LoggerFactory.getLogger;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import com.codetreatise.thuydienapp.event.EventTrigger;
import com.codetreatise.thuydienapp.view.FxmlView;
import com.sun.imageio.plugins.common.I18N;
import javafx.stage.Modality;
import lombok.Getter;
import org.slf4j.Logger;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

/**
 * Manages switching Scenes on the Primary Stage
 */
public class StageManager {
    private static final String TITLE = "title";
    private static final String STOP = "logout";
    private static final Logger LOG = getLogger(StageManager.class);
    @Getter
    private Stage primaryStage;
    private SpringFXMLLoader springFXMLLoader;
    private Stage dialog;
    public static I18N i18n;
    private static final String iconImageLoc =
            "/images/Mlogo-ico.png";
    private static final String instanceId = UUID.randomUUID().toString();
    private static final int FOCUS_REQUEST_PAUSE_MILLIS = 500;
    private static final int SINGLE_INSTANCE_LISTENER_PORT = 11055;
    private static final String SINGLE_INSTANCE_FOCUS_MESSAGE = "focus";
    private static StageManager instance;

    private StageManager() {

    }

    public static StageManager getInstance() {
        if(instance == null) {
            instance = new StageManager();
            return instance;
        }
        return instance;
    }

    public void setArgs(SpringFXMLLoader springFXMLLoader, Stage stage) {
        this.springFXMLLoader = springFXMLLoader;
        this.primaryStage = stage;
        dialog = new Stage();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
    }

    public void switchScene(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        show(viewRootNodeHierarchy, view.getTitle());
    }

    public void createModal(final FxmlView view) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
        // Một cửa sổ mới (Stage)
        Scene scene = new Scene(viewRootNodeHierarchy);
        scene.setRoot(viewRootNodeHierarchy);
        dialog.setTitle(view.getTitle());
        dialog.setScene(scene);
        dialog.sizeToScene();
        dialog.centerOnScreen();

        try {
            dialog.show();
        } catch (Exception exception) {
            logAndExit ("Unable to show scene for title" + view.getTitle(),  exception);
        }
    }
    private void show(final Parent rootnode, String title) {
        Scene scene = prepareScene(rootnode);
        scene.getStylesheets().add("/styles/Styles.css");
        
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);

        primaryStage.centerOnScreen();
        try {
            primaryStage.show();
        } catch (Exception exception) {
            logAndExit ("Unable to show scene for title" + title,  exception);
        }
    }
    
    private Scene prepareScene(Parent rootnode){
        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(rootnode);
        }
        scene.setRoot(rootnode);
        return scene;
    }

    /**
     * Loads the object hierarchy from a FXML document and returns to root node
     * of that hierarchy.
     *
     * @return Parent root node of the FXML document hierarchy
     */
    private Parent loadViewNodeHierarchy(String fxmlFilePath) {
        Parent rootNode = null;
        try {
            rootNode = springFXMLLoader.load(fxmlFilePath);
            Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
        } catch (Exception exception) {
            logAndExit("Unable to load FXML view" + fxmlFilePath, exception);
        }
        return rootNode;
    }
    
    
    private void logAndExit(String errorMsg, Exception exception) {
        LOG.error(errorMsg, exception, exception.getCause());
        Platform.exit();
    }


    public void setup() {
        CountDownLatch instanceCheckLatch = new CountDownLatch(1);

        Thread instanceListener = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(SINGLE_INSTANCE_LISTENER_PORT, 10)) {
                instanceCheckLatch.countDown();

                while (true) {
                    try (
                            Socket clientSocket = serverSocket.accept();
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(clientSocket.getInputStream()))
                    ) {
                        String input = in.readLine();
                        System.out.println("Received single instance listener message: " + input);
                        if (input.startsWith(SINGLE_INSTANCE_FOCUS_MESSAGE) && primaryStage != null) {
                            Thread.sleep(FOCUS_REQUEST_PAUSE_MILLIS);
                            Platform.runLater(() -> {
                                System.out.println("To front " + instanceId);
                                primaryStage.setIconified(false);
                                primaryStage.show();
                                primaryStage.toFront();
                            });
                        }
                    } catch (IOException e) {
                        System.out.println("Single instance listener unable to process focus message from client");
                        e.printStackTrace();
                    }
                }
            } catch(java.net.BindException b) {
                System.out.println("SingleInstanceApp already running");

                try (
                        Socket clientSocket = new Socket(InetAddress.getLocalHost(), SINGLE_INSTANCE_LISTENER_PORT);
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
                ) {
                    System.out.println("Requesting existing app to focus");
                    out.println(SINGLE_INSTANCE_FOCUS_MESSAGE + " requested by " + instanceId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Aborting execution for instance " + instanceId);
                Platform.exit();
            } catch(Exception e) {
                System.out.println(e.toString());
            } finally {
                instanceCheckLatch.countDown();
            }
        }, "instance-listener");
        instanceListener.setDaemon(true);
        instanceListener.start();

        try {
            instanceCheckLatch.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }



    public void init() {
        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);
        // sets up the tray icon (using awt code run on the swing thread).
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
        primaryStage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Mlogo.png"))));
    }

    public void showWhenHidden(final FxmlView view) {
        Platform.runLater(() -> {
            switchScene(view);
            primaryStage.toFront();
            primaryStage.setAlwaysOnTop(true);
            primaryStage.setAlwaysOnTop(false);
        });
    }


    /**
     * Sets up a system tray icon for the application.
     */
    private void addAppToTray(){
        java.awt.SystemTray tray = null;
        java.awt.TrayIcon trayIcon = null;
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();
            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.

            tray = java.awt.SystemTray.getSystemTray();
            URL imageLoc = getClass().getResource(iconImageLoc);
            java.awt.Image image = ImageIO.read(imageLoc);
            trayIcon = new java.awt.TrayIcon(image);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // if the user selects the default menu item (which includes the app name),
            // show the main app stage.
            java.awt.MenuItem openItem = new java.awt.MenuItem(getStringFromResourceBundle(TITLE));
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.MenuItem stopItem = new java.awt.MenuItem(getStringFromResourceBundle(STOP));
            stopItem.addActionListener(event -> Platform.runLater(this::stopService));
            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            java.awt.SystemTray finalTray = tray;
            TrayIcon finalTrayIcon = trayIcon;
            exitItem.addActionListener(event -> {
                Platform.exit();
                finalTray.remove(finalTrayIcon);
                System.exit(2);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.add(stopItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
        } catch (IOException  e) {
        } finally {
            // add the application tray icon to the system tray.
            if (tray != null) {
                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void stopService() {
        SystemArg.LOGIN = false;
        this.showWhenHidden(FxmlView.LOGIN);
    }


    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage() {
        if (primaryStage != null) {
            primaryStage.show();
            primaryStage.toFront();
        }
    }
    String getStringFromResourceBundle(String key){
        return ResourceBundle.getBundle("Bundle").getString(key);
    }

    public void closeDialog() {
        dialog.hide();
        dialog.close();
        EventTrigger.getInstance().setChange();
        EventTrigger.getInstance().notifyObservers(null);
    }
}
