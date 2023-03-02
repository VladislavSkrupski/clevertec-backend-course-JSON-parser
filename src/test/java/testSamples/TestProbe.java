package testSamples;

import java.util.*;

public class TestProbe {
    private static final String stringStaticFinalPrivate = "asd";
    private static String stringStaticPrivate;
    private transient int transientInt;
    private final String stringFinalPrivate = "asd";
    private double aDouble;
    private int anInt;
    boolean aBoolean;
    char aChar;
    float aFloat;
    short aShort;
    byte aByte;
    long aLong;
    public String[] stringsArray;
    GregorianCalendar calendar;
    protected List<Integer> list;
    Stack<String> stack = new Stack<>();
    List<TestProbeHelper> emptyTestProbeHelpers;
    Set<Double> set;
    private HashMap<String, Boolean> map;
    TestProbeHelper testProbeHelper;
    TestProbeHelper testProbeHelper2;
    List<TestProbeHelper> testProbeHelpers;

    public TestProbe() {
        this.transientInt = 1;
        this.aDouble = 1.2;
        this.anInt = 1283;
        this.aBoolean = false;
        this.aChar = '\n';
        this.aFloat = 2.3f;
        this.aShort = 1;
        this.aByte = 0b1111;
        this.aLong = 100L;
        this.stringsArray = new String[]{"asd", "asa", "ada"};
        this.calendar = new GregorianCalendar();
        this.list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        this.emptyTestProbeHelpers = new ArrayList<>();
        this.set = new TreeSet<>(Set.of(10.2, 145.2, 3123.3));
        this.map = new HashMap<>(Map.of("a", false, "b", true, "c", false));
        this.testProbeHelper = new TestProbeHelper();
        this.testProbeHelper2 = null;
        this.testProbeHelpers = new ArrayList<>(List.of(new TestProbeHelper(), new TestProbeHelper(), new TestProbeHelper()));
        this.stack.addAll(List.of("a", "b", "c"));
    }

    public void clear() {
        this.transientInt = 0;
        this.aDouble = 0;
        this.anInt = 0;
        this.aBoolean = false;
        this.aChar = 0;
        this.aFloat = 0;
        this.aShort = 0;
        this.aByte = 0;
        this.aLong = 0;
        this.stringsArray = null;
        this.calendar = null;
        this.list.clear();
        this.emptyTestProbeHelpers.clear();
        this.set.clear();
        this.map.clear();
        this.testProbeHelper = null;
        this.testProbeHelper2 = null;
        this.testProbeHelpers.clear();
        this.stack.clear();
    }
}
