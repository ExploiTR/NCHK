import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PingScale extends Application {
    private JButton start;
    private JPanel pane;
    private JScrollPane dtSr;
    private JScrollPane plSr;
    private JButton clr;
    private JComboBox<String> time;
    private JTextArea down;
    private JTextArea loss;
    private JButton dmp;
    private Calendar lCalendar;
    private Calendar nCalendar;
    private JFrame window;
    private ArrayList<DownTimeMem> mem;

    private static boolean is_down = false, shouldRun = false, exit = false, downInformed = false;
    private static long LAST_START_TIME = 0L, LAST_OKAY_TIME = 0L;

    private static long MINUTE_PERIODIC_TIME = 1L;

    private static long totalPacks = 0L, failedPacks = 0L;
    private Stage stage;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    @Override
    public void start(Stage primaryStage) {
        lCalendar = Calendar.getInstance();
        nCalendar = Calendar.getInstance();

        mem = new ArrayList<>();

        stage = primaryStage;

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

      /*  new Thread(() -> {
            try {
                setupTray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();*/

        initUI();
        showUI();

        Platform.setImplicitExit(false);
    }

    private void showUI() {
        window = new JFrame("N C H K - B Y - E X P L O I T R");
        window.setContentPane(pane);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                exit = true;
            }
        });

        down.setRows(5);
        loss.setRows(5);

        time.addItem("1");
        time.addItem("15");
        time.addItem("30");
        time.addItem("60");

        time.addItemListener(e -> MINUTE_PERIODIC_TIME = Long.parseLong(Objects.requireNonNull(time.getSelectedItem()).toString()));

        window.pack();
        window.setSize(640, 480);
        window.setVisible(true);

        monitorPacks();
    }

    private void initUI() {
        start.addActionListener((actionEvent) -> {
            shouldRun = !shouldRun;

            if (shouldRun)
                start.setText("STOP");
            else
                start.setText("START");

            lCalendar.setTimeInMillis(System.currentTimeMillis());
            lCalendar = Calendar.getInstance();
            down.append((shouldRun ? "Monitor Started" : "Monitor Stopped") + " at " +
                    timeFormat(lCalendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                    timeFormat(lCalendar.get(Calendar.MINUTE)) + ":" +
                    timeFormat(lCalendar.get(Calendar.SECOND)) + "\n");
        });

        clr.addActionListener((act) -> {
            down.setText("");
            loss.setText("");
        });

        dmp.addActionListener((event) -> showDump(window));

        down.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                dtSr.getVerticalScrollBar().setValue(dtSr.getVerticalScrollBar().getMaximum());
            }
        });
        loss.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                plSr.getVerticalScrollBar().setValue(plSr.getVerticalScrollBar().getMaximum());
            }
        });

        new Thread(() -> {
            while (!exit) {
                if (shouldRun) {
                    monitorDown();
                }
                try {
                    Thread.sleep(50);
                    // don't know why but it's required,
                    // loosing ping <50 , but will reduce
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showDump(JFrame yourFrame) {
        File file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\dump_" + System.currentTimeMillis() + ".txt");
        JOptionPane.showMessageDialog(yourFrame,
                "Dumping log to : " + file.getAbsolutePath(),
                "Dump Info",
                JOptionPane.INFORMATION_MESSAGE);

        new Thread(() -> {
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(obtainMessage()); //todo
                writer.flush();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(yourFrame,
                        "Error, Can\'t Access File",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }).start();
    }

    @org.jetbrains.annotations.NotNull
    @Contract(pure = true)
    private String obtainMessage() {
        Calendar xCalender = Calendar.getInstance();
        StringBuilder builder = new StringBuilder();
        builder.append("Network Statistics Info Generated with N C H K © 2019 Pratim Majumder\n" +
                "---------------------------------------------------------------------\nDate & Time : ")
                .append(timeFormat(xCalender.get(Calendar.DAY_OF_MONTH)))
                .append(".")
                .append(timeFormat(xCalender.get(Calendar.MONTH)))
                .append(".")
                .append(timeFormat(xCalender.get(Calendar.YEAR)))
                .append("  ")
                .append(timeFormat(xCalender.get(Calendar.HOUR_OF_DAY)))
                .append(":")
                .append(timeFormat(xCalender.get(Calendar.MINUTE)))
                .append(":")
                .append(timeFormat(xCalender.get(Calendar.SECOND)))
                .append("\n")
                .append("---------------------------------------------------------------------\n\n")
                .append("Downtime Info (24-Hour Clock Format)\n\n");

        DownTimeMem mem1;
        for (int i = 0; i < mem.size(); i++) {
            mem1 = mem.get(i);

            long tdfS = (mem1.getToMills() - mem1.getFromMills()) / 1000;
            long lhr = (int) (tdfS / 3600);
            long lmn = (int) ((tdfS - lhr * 3600) / 60);
            long lsc = (tdfS - (lhr * 3600 + lmn * 60));

            builder.append(i + 1)
                    .append(" > ")
                    .append("From ")
                    .append(timeFormat(mem1.getFromHr()))
                    .append(":")
                    .append(timeFormat(mem1.getFromMin()))
                    .append(":")
                    .append(timeFormat(mem1.getFromSec()))
                    .append(" to ")
                    .append(timeFormat(mem1.getToHr()))
                    .append(":")
                    .append(timeFormat(mem1.getToMin()))
                    .append(":")
                    .append(timeFormat(mem1.getToSec()))
                    .append("|")
                    .append(" Total ")
                    .append(timeFormat(lhr))
                    .append(":")
                    .append(timeFormat(lmn))
                    .append(":")
                    .append(timeFormat(lsc))
                    .append("\n");
        }
        builder.append("\n---------------------------------------------------------------------\n\n")
                .append("Packet Loss Info\n\n")
                .append("Total Packets Transmitted = ")
                .append(totalPacks)
                .append(" | Total Packets Failed = ")
                .append(failedPacks)
                .append(" | Loss (Reliability) = ")
                .append(String.format("%.2f", ((double) failedPacks / totalPacks) * 100))
                .append("%");

        return builder.append("\n\n---------------------------------------------------------------------").toString();
    }

    private void monitorDown() {
        if (executePing() != 0) {
            if (!is_down) {
                LAST_START_TIME = System.currentTimeMillis();
                is_down = true;
                downInformed = false;
            } else {
                if (System.currentTimeMillis() - LAST_OKAY_TIME >= TimeUnit.SECONDS.toMillis(5) && !downInformed) {
                    down.append("Downtime Started\n");
                    downInformed = true;
                }
            }
            totalPacks++;
            failedPacks++;
        } else {
            if (is_down) {
                lCalendar.setTimeInMillis(LAST_START_TIME);
                nCalendar.setTimeInMillis(System.currentTimeMillis());
                int diff = Math.toIntExact((System.currentTimeMillis() - LAST_START_TIME) / 1000);
                if (diff >= 5) {

                    mem.add(DownTimeMem.getTime(lCalendar.get(Calendar.HOUR_OF_DAY),
                            lCalendar.get(Calendar.MINUTE),
                            lCalendar.get(Calendar.SECOND),
                            nCalendar.get(Calendar.HOUR_OF_DAY),
                            nCalendar.get(Calendar.MINUTE),
                            nCalendar.get(Calendar.SECOND),
                            LAST_START_TIME,
                            nCalendar.getTimeInMillis()));

                    down.append(
                            String.format(Locale.US, "Connection Regained | Was down from %s:%s:%s to %s:%s:%s | Total %d sec\n",
                                    timeFormat(lCalendar.get(Calendar.HOUR_OF_DAY)),
                                    timeFormat(lCalendar.get(Calendar.MINUTE)),
                                    timeFormat(lCalendar.get(Calendar.SECOND)),
                                    timeFormat(nCalendar.get(Calendar.HOUR_OF_DAY)),
                                    timeFormat(nCalendar.get(Calendar.MINUTE)),
                                    timeFormat(nCalendar.get(Calendar.SECOND)),
                                    diff
                            ));
                }
                is_down = false;
            }
            totalPacks++;
            LAST_OKAY_TIME = System.currentTimeMillis();
        }
    }

    private void monitorPacks() {
        long preTime = System.currentTimeMillis();
        long postTime;
        while ((postTime = System.currentTimeMillis()) != 0L) {
            if (postTime - preTime > TimeUnit.MINUTES.toMillis(MINUTE_PERIODIC_TIME) && shouldRun) {
                preTime = postTime;
                loss.append(String.format(Locale.US, "Total packets sent = %d | Total packets dropped = %d\n", totalPacks, failedPacks));
            }
        }
    }

    private static int executePing() {
        try {
            Process process = Runtime.getRuntime().exec("ping google.com -n 1");
            Worker worker = new Worker(process);
            worker.start();
            worker.join(250); //max acceptable ping
            if (worker.exit != null)
                return worker.exit;
            else
                throw new TimeoutException();
        } catch (Exception e) {
            return -1;
        }
    }

    private static class Worker extends Thread {
        private final Process process;
        private Integer exit;

        private Worker(Process process) {
            this.process = process;
        }

        public void run() {
            try {
                exit = process.waitFor();
            } catch (Exception x) {
                exit = -1;
            }
        }
    }

    private static String timeFormat(long val) {
        if (val >= 10)
            return String.valueOf(val);
        else
            return "0" + val;
    }

   /* private void setupTray() throws Exception {
        if (!SystemTray.isSupported()) {
            return;
        }

        final PopupMenu popup = new PopupMenu();
        ImageView x = new ImageView();

        final TrayIcon trayIcon = new TrayIcon(ImageIO.read(createImage()));
        final SystemTray tray = SystemTray.getSystemTray();

        trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

        MenuItem displayMenu = new MenuItem("Hide Window");
        MenuItem aboutItem = new MenuItem("About");
        MenuItem exitItem = new MenuItem("Exit");

        displayMenu.addActionListener(event -> Platform.runLater(this::hideStage));

        aboutItem.addActionListener((event) -> {

        });

        exitItem.addActionListener((event) -> {
            System.exit(0);
        });

        popup.add(displayMenu);
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    } */

    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    private void hideStage() {
        if (stage != null) {
            stage.hide();
        }
    }

}
