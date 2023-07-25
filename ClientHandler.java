import java.io.*;
import java.net.*;
import weka.classifiers.*;
import weka.classifiers.bayes.*;
import weka.core.*;
import java.util.Arrays;
import java.util.ArrayList;
import weka.core.converters.*;

import java.util.List;
import java.util.regex.*;
import weka.classifiers.bayes.NaiveBayes;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private NaiveBayes naiveBayes;
    private Instances trainingInstances;
    private ArrayList<Attribute> attributes;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.attributes = new ArrayList<>(); // Initialize the attributes ArrayList here
        try {
            initNaiveBayesModel();
        } catch (IOException e) {
            System.err.println("Error initializing Naive Bayes model: " + e.getMessage());
        }
    }
    private String classifyEmail(String emailContent) {
        try {
            // Preprocess the email content and generate appropriate features
            Instance featureValues = extractFeatures(emailContent);

            // Create a dataset with the features and classify it using the Naive Bayes model
            Attribute classAttribute = trainingInstances.classAttribute();

            // Create a dataset with the attributes and add the instance
            Instances dataset = new Instances("TestInstances", attributes, 1);
            dataset.setClassIndex(dataset.numAttributes() - 1);
            dataset.add(featureValues);

            // Classify the instance and return the predicted class value
            double[] distribution = naiveBayes.distributionForInstance(dataset.firstInstance());
            int classification = Utils.maxIndex(distribution);
            return classAttribute.value(classification);

        } catch (Exception e) {
            System.err.println("Error classifying email: " + e.getMessage());
            return "Error";
        }
    }
    private Instance extractFeatures(String emailContent) {
        attributes.clear();
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (String attributeName : wordAttributes) {
            attributes.add(new Attribute(attributeName));
        }
        for (Character attributeName : charAttributes) {
            attributes.add(new Attribute(attributeName.toString()));
        }
        attributes.add(new Attribute("capital_run_length_average"));
        attributes.add(new Attribute("capital_run_length_longest"));
        attributes.add(new Attribute("capital_run_length_total"));
        attributes.add(new Attribute("class", Arrays.asList("0", "1")));

        Instances instances = new Instances("email_data", attributes, 0);
        instances.setClassIndex(instances.numAttributes() - 1);

        Instance instance = new DenseInstance(attributes.size());
        instance.setDataset(instances);

        String[] words = emailContent.toLowerCase().split("\\W+");
        String[] lines = emailContent.split("\\r?\\n");
        int capitalRunTotal = 0;
        int capitalRunLongest = 0;
        int capitalRunCount = 0;

        for (String line : lines) {
            Matcher capitalRunMatcher = Pattern.compile("([A-Z]+)").matcher(line);
            while (capitalRunMatcher.find()) {
                String capitalRun = capitalRunMatcher.group();
                capitalRunTotal += capitalRun.length();
                capitalRunLongest = Math.max(capitalRunLongest, capitalRun.length());
                capitalRunCount++;
            }
        }

        double capitalRunAverage = capitalRunCount > 0 ? (double) capitalRunTotal / capitalRunCount : 0;

        for (int i = 0; i < wordAttributes.size(); i++) {
            double count = 0;
            for (String word : words) {
                if (word.equals(wordAttributes.get(i))) {
                    count++;
                }
            }
            instance.setValue(attributes.get(i), count / words.length * 100);
        }

        for (int i = 0; i < charAttributes.size(); i++) {
            int finalI = i;
            double count = emailContent.chars().filter(ch -> ch == charAttributes.get(finalI)).count();
            instance.setValue(attributes.get(wordAttributes.size() + i), count / emailContent.length() * 100);
        }

        instance.setValue(attributes.get(attributes.size() - 4), capitalRunAverage);
        instance.setValue(attributes.get(attributes.size() - 3), capitalRunLongest);
        instance.setValue(attributes.get(attributes.size() - 2), capitalRunTotal);

        return instance;
    }


    private double[] calculateWordFrequencies(String[] words, String[] wordAttributes) {
        double[] wordFrequencies = new double[wordAttributes.length];
        int totalWords = words.length;

        for (int i = 0; i < wordAttributes.length; i++) {
            String targetWord = wordAttributes[i];
            int wordCount = 0;
            for (String word : words) {
                if (word.equalsIgnoreCase(targetWord)) {
                    wordCount++;
                }
            }
            wordFrequencies[i] = (double) wordCount / totalWords;
        }

        return wordFrequencies;
    }

    private double[] calculateCharacterFrequencies(String emailContent, String[] charAttributes) {
        double[] charFrequencies = new double[charAttributes.length];
        int totalChars = emailContent.length();

        for (int i = 0; i < charAttributes.length; i++) {
            char targetChar = charAttributes[i].charAt(0);
            int charCount = 0;
            for (char c : emailContent.toCharArray()) {
                if (c == targetChar) {
                    charCount++;
                }
            }
            charFrequencies[i] = (double) charCount / totalChars;
        }

        return charFrequencies;
    }

    private double[] calculateCapitalRunLengthFeatures(String emailContent) {
        Matcher capitalMatcher = Pattern.compile("[A-Z]+").matcher(emailContent);
        int totalCapitalLetters = 0;
        int longestCapitalRun = 0;
        int capitalRunCount = 0;

        while (capitalMatcher.find()) {
            String capitalRun = capitalMatcher.group();
            int runLength = capitalRun.length();
            totalCapitalLetters += runLength;
            longestCapitalRun = Math.max(longestCapitalRun, runLength);
            capitalRunCount++;
        }

        double averageCapitalRunLength = capitalRunCount == 0 ? 0 : (double) totalCapitalLetters / capitalRunCount;

        return new double[]{averageCapitalRunLength, longestCapitalRun, totalCapitalLetters};
    }
    private static final List<String> wordAttributes = Arrays.asList(
            "word_freq_make", "word_freq_address", "word_freq_all", "word_freq_3d", "word_freq_our",
            "word_freq_over", "word_freq_remove", "word_freq_internet", "word_freq_order",
            "word_freq_mail", "word_freq_receive", "word_freq_will", "word_freq_people",
            "word_freq_report", "word_freq_addresses", "word_freq_free", "word_freq_business",
            "word_freq_email", "word_freq_you", "word_freq_credit", "word_freq_your",
            "word_freq_font", "word_freq_000", "word_freq_money", "word_freq_hp", "word_freq_hpl",
            "word_freq_george", "word_freq_650", "word_freq_lab", "word_freq_labs", "word_freq_telnet",
            "word_freq_857", "word_freq_data", "word_freq_415", "word_freq_85", "word_freq_technology",
            "word_freq_1999", "word_freq_parts", "word_freq_pm", "word_freq_direct", "word_freq_cs",
            "word_freq_meeting", "word_freq_original", "word_freq_project", "word_freq_re",
            "word_freq_edu", "word_freq_table", "word_freq_conference"
    );

    private static final List<Character> charAttributes = Arrays.asList(
            ';', '(', '[', '!', '$', '#'
    );



    private void initNaiveBayesModel() throws IOException {
        naiveBayes = new NaiveBayes();

        String dataset = "spambase.arff";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(dataset));
        Instances datasetInstances = new Instances(bufferedReader);

        datasetInstances.randomize(new java.util.Random(0));
        int trainingDataSize = (int) Math.round(datasetInstances.numInstances() * 0.66);

        trainingInstances = new Instances(datasetInstances, 0, trainingDataSize);
        trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);

        Instances testInstances = new Instances(datasetInstances, trainingDataSize, datasetInstances.numInstances() - trainingDataSize);
        testInstances.setClassIndex(testInstances.numAttributes() - 1);

        bufferedReader.close();

        try {
            naiveBayes.buildClassifier(trainingInstances);
            double accuracy = calculateAccuracy(testInstances);
            System.out.println("Classifier accuracy: " + accuracy * 100 + "%");
        } catch (Exception e) {
            System.err.println("Error building Naive Bayes classifier: " + e.getMessage());
        }
    }


    private double calculateAccuracy(Instances testInstances) {
        int correct = 0;
        for (int i = 0; i < testInstances.numInstances(); i++) {
            try {
                Instance instance = testInstances.instance(i);
                double[] distribution = naiveBayes.distributionForInstance(instance);
                int predictedClass = Utils.maxIndex(distribution);
                int actualClass = (int) instance.classValue();

                if (predictedClass == actualClass) {
                    correct++;
                }
            } catch (Exception e) {
                System.err.println("Error calculating accuracy: " + e.getMessage());
            }
        }
        return (double) correct / testInstances.numInstances();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String emailContent = in.readLine();
            String spamStatus = classifyEmail(emailContent);
            out.println(spamStatus);

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }

   /* private String classifyEmail(String emailContent) {
        // Create an instance with the email content and classify it using the Naive Bayes model
        try {
            // Replace the attributes below with the actual attributes of your dataset
            Attribute emailAttribute = new Attribute("emailContent", true);
            Attribute classAttribute = new Attribute("class", Arrays.asList("spam", "not_spam"));

            ArrayList<Attribute> attributes = new ArrayList<>();
            attributes.add(emailAttribute);
            attributes.add(classAttribute);

            Instances instances = new Instances("TestInstances", attributes, 0);
            instances.setClassIndex(1);

            DenseInstance instance = new DenseInstance(2);
            instance.setValue(emailAttribute, emailContent);
            instances.add(instance);

            double[] distribution = naiveBayes.distributionForInstance(instances.instance(0));
            int classification = Utils.maxIndex(distribution);
            return classAttribute.value(classification);

        } catch (Exception e) {
            System.err.println("Error classifying email: " + e.getMessage());
            return "Error";
        }
    }*/
}
