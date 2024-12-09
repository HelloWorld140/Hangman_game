import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Hangman {
    private static final File DICTIONARY = new File("singular.txt");
    private static final List<String> LIST_OF_ALL_WORDS = new ArrayList<>();
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final List<Character> WORD = new ArrayList<>();
    private static final List<Character> HIDDEN_WORD = new ArrayList<>();
    private static final List<Character> INPUT_LETTERS = new ArrayList<>();
    private static final int MAX_COUNT_OF_MISTAKES = 16;
    private static final int MIN_WORD_LENGTH = 5;
    private static final int MAX_WORD_LENGTH = 13;
    private static final char HIDDEN_LETTER = '□';
    private static final String SPACE = " ";
    private static final String HYPHEN = "-";
    private static final String YES = "д";
    private static final String NO = "н";
    private static final int EASY = 2;
    private static final int MEDIUM = 3;
    private static final int HARD = 4;
    private static final String[][] HANGMAN_PICTURE = new String[][] {
            {"""
                         
                         
                         
                         
                    |
                    |\
            """, """
                          
                    |     
                    |
                    |
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |
                    |
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |    0
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |   /0
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |   /0\\
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |   /0\\
                    |   /
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |   /0\\
                    |   / \\
                    |\
            """},
            {"""
                    ＿＿＿＿
                    |    |
                    |
                    |
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |    0
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |   /0
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |   /0\\
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |   /0\\
                    |   /
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |   /0\\
                    |   / \\
                    |\
            """}, {"""
                    ＿＿＿＿
                    |    |
                    |
                    |
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |    0
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |   /0\\
                    |
                    |\
            """, """
                    ＿＿＿＿
                    |    |
                    |    o
                    |   /0\\
                    |   / \\
                    |\
            """}
    };
    private static Difficulty currentDifficulty;
    private static String letter;
    private static int counterOfMistakes;

    public static void main(String[] args) {
        createListOfAllWords();
        mainGameLoop();
    }

    private static void createListOfAllWords() {
        try {
            Scanner scanner = new Scanner(DICTIONARY);
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                if (isCorrectWord(word)) {
                    LIST_OF_ALL_WORDS.add(word);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        }
    }

    private static void mainGameLoop() {
        while (startOrEndGame()) {
            choiceDifficultyInput();
            takeRandomWord();
            createHiddenWord();
            System.out.println("Для проверки работоспособности выводим загаданное слово: " + WORD);
            while (inGame()) {
                print();
                printHiddenWord();
                printInputLetter();
                printCounterOfMistakes();
                char letterAsChar = inputLetter();
                if (checkAlreadyInputLetter(letterAsChar)) {
                    System.out.println("Вы уже вводили эту букву!");
                    continue;
                }
                addLetterInListOfInputLetter(letterAsChar);
                if (checkInputLetterInWord(letterAsChar)) {
                    System.out.println("Вы угадали букву!");
                    openLetterInHiddenWord(letterAsChar);
                } else {
                    System.out.println("Вы не угадали букву!");
                    counterOfMistakes++;
                }
            }
            printResultOfGame();
            refreshGame();
        }
    }

    private static boolean isCorrectWord(String word) {
        return !word.contains(SPACE) && !word.contains(HYPHEN)
                && (word.length() > MIN_WORD_LENGTH) && (word.length() < MAX_WORD_LENGTH);
    }

    private static boolean startOrEndGame() {
        boolean playerChoice;
        while (true) {
            System.out.printf("\nДля начала игры введите (%s).\nДля закрытия введите (%s)\n", YES, NO);
            letter = SCANNER.nextLine().toLowerCase(Locale.ROOT);
            if (!letter.equals(YES) && !letter.equals(NO)) {
                System.out.println("Вы ввели не ту букву");
            } else if (letter.equals(YES)) {
                playerChoice = true;
                break;
            }
            if (letter.equals(NO)) {
                System.out.println("До скорой встречи!");
                playerChoice = false;
                break;
            }
        }
        return playerChoice;

    }

    private static void choiceDifficultyInput() {
        while (true) {
            System.out.printf("Выберите уровень сложности введя цифру: \nЛегко (%d)\nСредне (%d)\nСложно (%d)\n",
                    Difficulty.EASY.ordinal(),
                    Difficulty.MEDIUM.ordinal(),
                    Difficulty.HARD.ordinal());
            letter = SCANNER.nextLine();
            if (letter.length() != 1) {
                System.out.println("Вы ввели больше 1 цифры.");
            }
             else if (!Character.isDigit(letter.charAt(0))) {
                System.out.println("Вы ввели не цифру.");
            } else if (Integer.parseInt(letter) > Difficulty.HARD.ordinal() || Integer.parseInt(letter) < Difficulty.EASY.ordinal()) {
                System.out.println("Вы ввели неверную цифру.");
            } else {
                 currentDifficulty = Difficulty.values()[Integer.parseInt(letter)];
                break;
            }
        }
    }

    private static void takeRandomWord() {
        Random randomNumber = new Random();
        String word = LIST_OF_ALL_WORDS.get(randomNumber.nextInt(LIST_OF_ALL_WORDS.size()));
        for (int i = 0; i < word.length(); i++) {
            WORD.add(word.charAt(i));
        }
    }

    private static void createHiddenWord() {
        for (int i = 0; i < WORD.size(); i++) {
            HIDDEN_WORD.add(HIDDEN_LETTER);
        }
    }

    private static void printHiddenWord() {
        for (char letter : HIDDEN_WORD)
            System.out.print(letter + " ");
    }

    private static void printCounterOfMistakes() {
        System.out.printf("Количество ошибок: %d из %d\n", counterOfMistakes, currentDifficulty.getMaxMistakes());
    }

    private static char inputLetter() {
        while (true) {
            System.out.println("Введите 1 русскую букву");
            letter = SCANNER.nextLine().toLowerCase();
            if (letter.length() != 1) {
                System.out.println("Вы вввели больше 1 буквы. Введите 1 русскую букву");
            } else if (!Character.UnicodeBlock.of(letter.charAt(0)).equals(Character.UnicodeBlock.CYRILLIC)) {
                System.out.println("Вы ввели не русскую букву. Введите 1 русскую букву");
            } else return letter.charAt(0);
        }
    }

    private static boolean checkInputLetterInWord(char letter) {
        return WORD.contains(letter);
    }

    private static void openLetterInHiddenWord(char letter) {
            for (int i = 0; i < WORD.size(); i++) {
                if (WORD.get(i) == letter) {
                    HIDDEN_WORD.set(i, letter);
                }
            }
    }

    private static boolean checkAlreadyInputLetter(char letter) {
        return INPUT_LETTERS.contains(letter);
    }

    private static void addLetterInListOfInputLetter(char letter) {
        INPUT_LETTERS.add(letter);
    }

    private static void printInputLetter() {
        System.out.println("\nРанее введеные буквы: " + INPUT_LETTERS);
    }

    private static void printResultOfGame() {
        if (isWin()) {
            System.out.println("Поздравлю, вы победили! \nЗагаданное слово: ");
            printWord();
        } else if (isLose()) {
            print();
            System.out.print("Вы проиграли :( \nЗагаданное слово: ");
            printWord();
        }
    }

    private static boolean inGame() {
        return !isWin() && !isLose();
    }

    private static boolean isWin() {
        return WORD.containsAll(HIDDEN_WORD);
    }

    private static boolean isLose() {
        return counterOfMistakes == currentDifficulty.getMaxMistakes();
    }

    private static void printWord() {
        for (char letter : WORD)
            System.out.print(letter + " ");
    }

    private static void refreshGame() {
        WORD.clear();
        HIDDEN_WORD.clear();
        INPUT_LETTERS.clear();
        counterOfMistakes = 0;
    }

    private static void print() {
        System.out.println(HANGMAN_PICTURE[currentDifficulty.ordinal()][counterOfMistakes]);
    }
}