public enum Difficulty {
    EASY(8),
    MEDIUM(6),
    HARD(4);
    private int  maxMistakes;
    Difficulty(int maxMistakes) {
        this.maxMistakes = maxMistakes;
    }

    public int getMaxMistakes() {
        return maxMistakes;
    }
}
