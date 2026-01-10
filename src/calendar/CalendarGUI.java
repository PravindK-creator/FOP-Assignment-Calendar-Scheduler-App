/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package calendar;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import calendar.Event;
public class CalendarGUI extends JFrame {
        private List<Event> allEvents;
        private YearMonth currentYearMonth;
        private JLabel monthLabel;
        private JPanel calendarPanel;

        public CalendarGUI(List<Event> events) {
            this.allEvents = events;
            this.currentYearMonth = YearMonth.now();

            // 1. Set the basic properties of the window
            setTitle("My Calendar =-=");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            // 2.  Top control bar
            JPanel topPanel = new JPanel();
            JButton prevBtn = new JButton("< Prev");
            JButton nextBtn = new JButton("Next >");
            monthLabel = new JLabel("", SwingConstants.CENTER);
            monthLabel.setFont(new Font("Arial", Font.BOLD, 20));

            // Button click event
            prevBtn.addActionListener(e -> {
                currentYearMonth = currentYearMonth.minusMonths(1);
                refreshCalendar();
            });
            nextBtn.addActionListener(e -> {
                currentYearMonth = currentYearMonth.plusMonths(1);
                refreshCalendar();
            });

            topPanel.setLayout(new BorderLayout());
            topPanel.add(prevBtn, BorderLayout.WEST);
            topPanel.add(monthLabel, BorderLayout.CENTER);
            topPanel.add(nextBtn, BorderLayout.EAST);
            add(topPanel, BorderLayout.NORTH);

            // 3. Calendar grid area
            calendarPanel = new JPanel();
            calendarPanel.setLayout(new GridLayout(0, 7));
            add(calendarPanel, BorderLayout.CENTER);
           
            // 4. Initialize display
            refreshCalendar();

            // Center the window and display it.
            setLocationRelativeTo(null);
            setVisible(true);
        }

    private void refreshCalendar() {
        calendarPanel.removeAll(); // clear the old edition

        String monthName = currentYearMonth.getMonth().toString();
        monthLabel.setText(monthName + " " + currentYearMonth.getYear());

        // 2. Change the colors
        Color headerColor = Color.PINK;
        Color headerText = Color.GREEN;
        Color eventBg = Color.RED;
        Color noEventBg = Color.WHITE;

        // 3. Font settings
        Font mainFont = new Font("Arial", Font.PLAIN, 14);
        Font boldFont = new Font("Arial", Font.BOLD, 16);
        Font headerFont = new Font("Arial", Font.BOLD, 14);

        String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(headerFont);
            label.setOpaque(true);
            label.setBackground(headerColor);
            label.setForeground(headerText);
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
            calendarPanel.add(label);
        }

        // Calculate the blank cells.
        LocalDate firstDay = currentYearMonth.atDay(1);
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int startDayOfWeek = firstDay.getDayOfWeek().getValue();
        int emptySlots = (startDayOfWeek == 7) ? 0 : startDayOfWeek;

        // C. Filling space
        for (int i = 0; i < emptySlots; i++) {
            JLabel empty = new JLabel("");
            empty.setOpaque(true);
            empty.setBackground(Color.decode("#F5F5F5"));
            empty.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            calendarPanel.add(empty);
        }

        // D. Date filling button
        Color lightGrayBg = Color.decode("#F9F9F9");
        Color hoverBg = Color.decode("#E0E0E0");

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            JButton dayBtn = new JButton(String.valueOf(day));

            // Basic style beautification
            dayBtn.setFont(new Font("Arial", Font.BOLD, 16));
            dayBtn.setFocusPainted(false);
            dayBtn.setBorderPainted(false);
            dayBtn.setBackground(lightGrayBg);
            dayBtn.setOpaque(true);

            // Check if there are any activities.
            List<Event> daysEvents = getEventsForDate(date);
            if (!daysEvents.isEmpty()) {
                // On event days: turn red and add stars.
                dayBtn.setText("<html><center>" + day + "<br>*</center></html>");
                dayBtn.setBackground(eventBg);
                dayBtn.setForeground(Color.WHITE);

                // Click event: Icon code has been added here.
                dayBtn.addActionListener(e -> {
                    String message = buildEventMessage(date, daysEvents);

                    //Read custom images:icon.png
                    ImageIcon originalIcon = new ImageIcon("icon.png");

                    //  Zoom in/out the picture
                    Image img = originalIcon.getImage();
                    Image scaledImg = img.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                    ImageIcon finalIcon = new ImageIcon(scaledImg);

                    // A pop-up window appears.
                    JOptionPane.showMessageDialog(this,
                            message,
                            "Event Details",
                            JOptionPane.PLAIN_MESSAGE,
                            finalIcon);
                });
            }

            calendarPanel.add(dayBtn);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

        //Find all activities of a certain day using the stream filtering feature of Java 8.
        private List<Event> getEventsForDate(LocalDate date) {
            return allEvents.stream()
                    .filter(e -> e.getStartDateTime().toLocalDate().equals(date))
                    .collect(Collectors.toList());
        }

    // Generate pop-up text
    private String buildEventMessage(LocalDate date, List<Event> events) {
        StringBuilder sb = new StringBuilder();
        // Define a formatter that displays AM/PM.
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        sb.append("Events for ").append(date).append(":\n\n");

        for (Event e : events) {
            sb.append("~").append(e.getTitle())

                    .append("\n   Time: ").append(e.getStartDateTime().format(timeFormatter))
                    .append("\n   Desc: ").append(e.getDescription()).append("\n\n");
        }
        return sb.toString();
    }
    }
