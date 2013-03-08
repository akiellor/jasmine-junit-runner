package jasmine.junit;

@JasmineSuite class DefaultSuite {
    public static JasmineSuite getAnnotation(){
        return DefaultSuite.class.getAnnotation(JasmineSuite.class);
    }
}
