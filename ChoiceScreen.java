import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ChoiceScreen extends JFrame {
    JButton start_btn;
    String initial_array_size = "20";
    private final int window_width = 1000;
    private final int window_height = 700;
    public ArrayList<Integer> array;
    JPanel general;
    public JPanel lower_panel;
    JTextField array_size_tf;
    JLabel array_size_error;
    JComboBox<String> algorithms_cb;
    JSlider speed_slider;
    int[] speeds;

    public ChoiceScreen(){
        setTitle("Choice Screen");
        setResizable(false);
        setSize(window_width,window_height);
        setLocationRelativeTo(null);

        array = new ArrayList<>();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });

        init();

        setVisible(true);
    }

    public void init(){
        general = new JPanel(new GridLayout(2,1));

        // UPPER PANEL
        JPanel upper_panel = new JPanel(new GridLayout(1,3));

        // first column upper panel init
        JPanel first_upper_panel = new JPanel(new FlowLayout());

        JPanel algorithm_choice_panel = new JPanel(new GridLayout(2,1));
        algorithm_choice_panel.add(new JLabel("Sorting Algorithm:"));
        algorithms_cb = new JComboBox<>();
        algorithms_cb.addItem("Selection Sort");
        algorithms_cb.addItem("Bubble Sort");
        algorithms_cb.addItem("Insertion Sort");
        algorithms_cb.addItem("Merge Sort");
        algorithms_cb.addItem("Quick Sort");
        algorithm_choice_panel.add(algorithms_cb);

        JPanel array_size_panel = new JPanel(new GridLayout(2,1));
        array_size_panel.add(new JLabel("Array Size:"));

        // ADD ERROR MESSAGE UPON ENTERING NON VALID STRING AND IMPLEMENT RESPONSIVE UPDATE ON LOWER PANEL
        array_size_error = new JLabel("ONLY VALID NUMBERS!");
        array_size_error.setForeground(Color.RED);
        array_size_error.setVisible(false);

        array_size_tf = new JTextField();
        array_size_tf.setText(initial_array_size);
        array_size_tf.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try{
                    Integer.parseInt(array_size_tf.getText());
                    array_size_error.setVisible(false);
                    start_btn.setEnabled(true);

                    FillArray();
                    RepaintBottomPanel();
                }catch (NumberFormatException exception){

                    // SHOW ERROR UPON INCORRECT INPUT
                    array_size_error.setVisible(true);
                    start_btn.setEnabled(false);

                    revalidate();
                    repaint();
                }
            }
        });
        array_size_panel.add(array_size_tf);
        array_size_panel.add(array_size_error);

        first_upper_panel.add(algorithm_choice_panel);
        first_upper_panel.add(array_size_panel);

        upper_panel.add(first_upper_panel);

        // second column upper panel init
        JPanel second_upper_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // START BUTTON
        start_btn = new JButton("Start");
        start_btn.setFont(new Font(start_btn.getFont().getFontName(),Font.PLAIN,16));
        start_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start_btn.setEnabled(false);
                array_size_tf.setEnabled(false);
                ShuffleArray();
//                SortingAlgorithms.RunAlgorithm(algorithms_cb.getSelectedItem().toString(),array);

                switch (algorithms_cb.getSelectedItem().toString()){
                    case "Selection Sort":
                        SelectionSort();
                        break;
                    case "Bubble Sort":
                        BubbleSort();
                        break;
                    case "Insertion Sort":
                        InsertionSort();
                        break;
                    case "Merge Sort":
                        MergeSort();
                        break;
                    case "Quick Sort":
                        QuickSort();
                        break;
                }
            }
        });

        // STOP BUTTON
        JButton stop_btn = new JButton("Stop");
        stop_btn.setFont(new Font(stop_btn.getFont().getFontName(),Font.PLAIN,16));
        stop_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sorting_thread.interrupt();
                start_btn.setEnabled(true);
                array_size_tf.setEnabled(true);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 50;
        gbc.ipady = 25;
        second_upper_panel.add(start_btn,gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10,0,0,0);
        gbc.gridy = 1;
        second_upper_panel.add(stop_btn,gbc);
        upper_panel.add(second_upper_panel);

        // third column upper panel init
        JPanel third_upper_panel = new JPanel(new FlowLayout());
        JPanel speed_panel = new JPanel(new GridLayout(2,1));
        speed_panel.add(new JLabel("Sorting Speed:"));

        speeds = new int[6];
        for (int i = 0; i < 6; i++) {
            speeds[i] = ((5-i)*10)+1;
        }

        // SPEED SLIDER
        speed_slider = new JSlider(1,6);
        speed_slider.setLabelTable(speed_slider.createStandardLabels(1,1));
        speed_slider.setPaintLabels(true);
        speed_panel.add(speed_slider);

        third_upper_panel.add(speed_panel);
        upper_panel.add(third_upper_panel);

        general.add(upper_panel);
        FillArray();

        // LOWER PANEL
        lower_panel = new JPanel(new GridLayout(1,array.size()));
        lower_panel.setBackground(Color.BLACK);
        RepaintBottomPanel();

        general.add(lower_panel);

        add(general);
    }

    // FUNCTION FOR REPAINTING VISUAL OF ARRAY
    public void RepaintBottomPanel(){
        // initialize lower panel
        general.remove(lower_panel);
        lower_panel = new JPanel(new GridLayout(1,array.size()));
        lower_panel.setBackground(Color.BLACK);
        System.out.println((window_width/array.size())+100);

        // painting visually array
        for(int i = 0;i<array.size();i++){
            int i2 = i;
            lower_panel.add(new JComponent() {
                @Override
                public void paint(Graphics g) {
                    g.setColor(Color.WHITE);
                    g.fillRect(1,(int) (((window_height)/2-30)*((float)(array.size()-array.get(i2))/array.size()))+5,window_width/array.size()+10, (int) (((window_height)/2-30)*((float)array.get(i2)/array.size()))); // 5,5,-10,-30
                    // array.size()<20?5:array.size()<50?4:array.size()<100?3:2
                    // (array.size()<20?10:array.size()<50?8:array.size()<100?6:4)
                }
            });
        }

        general.add(lower_panel);
        revalidate();
        repaint();
    }

    // FUNCTION FOR SHUFFLING THE ARRAY
    static Thread worker_thread;
    public void ShuffleArray(){
        Random rnd = new Random();
        worker_thread = new Thread(){
            public void run(){
                for(int i = 0;i<array.size();i++){
                    int i2 = rnd.nextInt(0,array.size());
                    int t = array.get(i);
                    array.set(i,array.get(i2));
                    array.set(i2,t);

                    RepaintBottomPanel();
                    try {
                        Thread.sleep(1000/array.size());
                    } catch (InterruptedException e) {
                        System.out.println("Worker thread interrupted.");
                        return;
                    }
                }
                System.out.println("Done Shuffling");
            }
        };

        worker_thread.start();
    }

    // FUNCTION FOR FILLING THE ARRAY
    public void FillArray(){
        array = new ArrayList<>();
        for (int i = 0;i<Integer.parseInt(array_size_tf.getText());i++){
            array.add(i+1);
        }
    }

    // SORTING ALGORTIHMS
    public Thread sorting_thread;
    public void SelectionSort(){
        sorting_thread = new Thread (){
            public void run(){
                try {
                    worker_thread.join();
                } catch (InterruptedException e) {
                    System.out.println("Join interrupted.");
                    worker_thread.interrupt();
                    FillArray();
                    RepaintBottomPanel();
                    return;
                }
                System.out.println("Start Sorting");

                // START SORTING
                for(int i = 0;i<array.size();i++){
                    for(int j = i+1;j<array.size();j++){
                        if(array.get(i) > array.get(j)){
                            int t = array.get(i);
                            array.set(i,array.get(j));
                            array.set(j,t);

                            // REPAINT
                            RepaintBottomPanel();
                            try {
                                Thread.sleep(speeds[speed_slider.getValue()-1]);
                            } catch (InterruptedException e) {
                                System.out.println("Sorting thread interrupted");
                                FillArray();
                                RepaintBottomPanel();
                                return;
                            }
                        }
                    }
                }
                start_btn.setEnabled(true);
                array_size_tf.setEnabled(true);
            }
        };
        sorting_thread.start();
    }

    public void BubbleSort(){
        sorting_thread = new Thread(){
            public void run(){
                try {
                    worker_thread.join();
                } catch (InterruptedException e) {
                    System.out.println("Join interrupted.");
                    worker_thread.interrupt();
                    FillArray();
                    RepaintBottomPanel();
                    return;
                }
                System.out.println("Start Sorting");

                // START SORTING
                for(int i = 0;i<array.size();i++){
                    for(int j = 0;j<array.size() - 1 - i;j++){
                        if(array.get(j) > array.get(j+1)){
                            int t = array.get(j);
                            array.set(j,array.get(j+1));
                            array.set(j+1,t);

                            // REPAINT
                            RepaintBottomPanel();
                            try {
                                Thread.sleep(speeds[speed_slider.getValue()-1]);
                            } catch (InterruptedException e) {
                                System.out.println("Sorting thread interrupted");
                                FillArray();
                                RepaintBottomPanel();
                                return;
                            }
                        }
                    }
                }
                start_btn.setEnabled(true);
                array_size_tf.setEnabled(true);
            }
        };
        sorting_thread.start();
    }

    public void InsertionSort(){
        sorting_thread = new Thread(){
            public void run(){
                try {
                    worker_thread.join();
                } catch (InterruptedException e) {
                    System.out.println("Join interrupted.");
                    worker_thread.interrupt();
                    FillArray();
                    RepaintBottomPanel();
                    return;
                }
                System.out.println("Start Sorting");

                // START SORTING
                int j;
                for(int i = 1;i<array.size();i++){
                    int ce = array.get(i);
                    j = i-1;
                    while(j >= 0 && array.get(j) > ce){
                        array.set(j+1,array.get(j));
                        j--;

                        // REPAINT
                        RepaintBottomPanel();
                        try {
                            Thread.sleep(speeds[speed_slider.getValue()-1]);
                        } catch (InterruptedException e) {
                            System.out.println("Sorting thread interrupted");
                            FillArray();
                            RepaintBottomPanel();
                            return;
                        }
                    }
                    array.set(j+1,ce);

                    // REPAINT
                    RepaintBottomPanel();
                    try {
                        Thread.sleep(speeds[speed_slider.getValue()-1]);
                    } catch (InterruptedException e) {
                        System.out.println("Sorting thread interrupted");
                        FillArray();
                        RepaintBottomPanel();
                        return;
                    }
                }
                start_btn.setEnabled(true);
                array_size_tf.setEnabled(true);
            }
        };
        sorting_thread.start();
    }

    public void MergeSort(){
        sorting_thread = new Thread(){
            public void run(){
                try {
                    worker_thread.join();
                } catch (InterruptedException e) {
                    System.out.println("Join interrupted.");
                    worker_thread.interrupt();
                    FillArray();
                    RepaintBottomPanel();
                    return;
                }
                System.out.println("Start Sorting");

                // START SORTING
                sort(0,array.size()-1);

                start_btn.setEnabled(true);
                array_size_tf.setEnabled(true);
            }
            private void sort(int l, int r){
                if(l < r){
                    int m = l + (r - l) / 2;

                    sort(l,m);
                    sort(m+1,r);

                    merge(l,m,r);
                }
            }
            private void merge(int l, int m, int r){
                int n1 = m - l + 1;
                int n2 = r - m;

                int[] L = new int[n1];
                int[] R = new int[n2];

                for (int i = 0; i < n1; i++) {
                    L[i] = array.get(i + l);
                }

                for (int j = 0; j < n2; j++) {
                    R[j] = array.get(j + m + 1);
                }
                
                int i = 0; int j = 0; int k = l;

                while (i < n1 && j < n2){
                    if(L[i] < R[j]){
                        array.set(k,L[i]);
                        i++;

                        // REPAINT
                        RepaintBottomPanel();
                        try {
                            Thread.sleep(speeds[speed_slider.getValue()-1]);
                        } catch (InterruptedException e) {
                            System.out.println("Sorting thread interrupted");
                            FillArray();
                            RepaintBottomPanel();
                            return;
                        }
                    }
                    else{
                        array.set(k,R[j]);
                        j++;

                        // REPAINT
                        RepaintBottomPanel();
                        try {
                            Thread.sleep(speeds[speed_slider.getValue()-1]);
                        } catch (InterruptedException e) {
                            System.out.println("Sorting thread interrupted");
                            FillArray();
                            RepaintBottomPanel();
                            return;
                        }
                    }
                    k++;
                }

                while(i < n1){
                    array.set(k,L[i]);
                    i++;
                    k++;

                    // REPAINT
                    RepaintBottomPanel();
                    try {
                        Thread.sleep(speeds[speed_slider.getValue()-1]);
                    } catch (InterruptedException e) {
                        System.out.println("Sorting thread interrupted");
                        FillArray();
                        RepaintBottomPanel();
                        return;
                    }
                }

                while(j < n2){
                    array.set(k,R[j]);
                    j++;
                    k++;

                    // REPAINT
                    RepaintBottomPanel();
                    try {
                        Thread.sleep(speeds[speed_slider.getValue()-1]);
                    } catch (InterruptedException e) {
                        System.out.println("Sorting thread interrupted");
                        FillArray();
                        RepaintBottomPanel();
                        return;
                    }
                }
            }
        };
        sorting_thread.start();
    }

    public void QuickSort(){
        sorting_thread = new Thread(){
            public void run(){
                try {
                    worker_thread.join();
                } catch (InterruptedException e) {
                    System.out.println("Join interrupted.");
                    worker_thread.interrupt();
                    FillArray();
                    RepaintBottomPanel();
                    return;
                }
                System.out.println("Start Sorting");

                // START SORTING
                sort(0,array.size()-1);

                start_btn.setEnabled(true);
                array_size_tf.setEnabled(true);
            }

            private void sort(int l, int r){
                int pivotI = r;
                int smallI = -1;

                if(l == r || l == -1 || r == -1) return;

                for (int i = l; i < r; i++) {
                    if(array.get(i) < array.get(r) ){
                        int t = array.get(smallI+1);
                        array.set(smallI+1,array.get(i));
                        array.set(i,t);
                        smallI++;

                        // REPAINT
                        RepaintBottomPanel();
                        try {
                            Thread.sleep(speeds[speed_slider.getValue()-1]);
                        } catch (InterruptedException e) {
                            System.out.println("Sorting thread interrupted");
                            FillArray();
                            RepaintBottomPanel();
                            interrupt();
                            return;
                        }
                    }
                }
                pivotI = smallI+1;

                int t = array.get(pivotI);
                array.set(pivotI,array.get(r));
                array.set(r,t);

                // REPAINT
                RepaintBottomPanel();
                try {
                    Thread.sleep(speeds[speed_slider.getValue()-1]);
                } catch (InterruptedException e) {
                    System.out.println("Sorting thread interrupted");
                    FillArray();
                    RepaintBottomPanel();
                    interrupt();
                    return;
                }

                sort(l,pivotI-1);
                sort(pivotI+1,r);
            }
        };
        sorting_thread.start();
    }

    public void HeapSort(){

    }

    public void CountingSort(){

    }

    public void RadixSort(){

    }

    public void BucketSort(){}

    public void BingoSort(){}

    public void ShellSort(){}

    public void TimSort(){}

    public void CombSort(){}

    public void PigeonholeSort(){}

    public void CycleSort(){}

    public void CoctailSort(){}

    public void StrandSort(){}

    public void BitonicSort(){}

    public void PancakeSort(){}

    public void PermutationSort(){}

    public void GnomeSort(){}

    public void SleepSort(){}

    public void StoogeSort(){}

    public void TreeSort(){}

    public void BrickSort(){}

    public void ThreeWayMergeSort(){}
}
