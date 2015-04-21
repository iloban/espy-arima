package org.espy.arima;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class ArimaFitterTest {
    @Test
    public void test() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
            arimaProcess.setArCoefficients(generateCoefficients(random));
            arimaProcess.setMaCoefficients(generateCoefficients(random));
            arimaProcess.setIntegrationOrder(random.nextInt(4));
            arimaProcess.setConstant(random.nextGaussian());
            arimaProcess.setVariation(Math.abs(random.nextGaussian()));
            ArimaProcessRealization arimaProcessRealization = new DefaultArimaProcessRealization(arimaProcess);
            double[] observations = arimaProcessRealization.next(50);
            ArimaProcess fittedArimaProcess =
                    ArimaFitter.fit(observations, arimaProcess.getArOrder(), arimaProcess.getMaOrder());
            System.out.println("=================");
            System.out.println("Source: " + arimaProcess);
            System.out.println("-----------------");
            System.out.println("Fitted: " + fittedArimaProcess + "\n");
        }
    }

    private static double[] generateCoefficients(Random random) {
        double[] coefficients = new double[random.nextInt(3)];
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = random.nextGaussian();
        }
        return coefficients;
    }

    @Test
    public void test2() {
        DefaultArimaProcess arimaProcess = new DefaultArimaProcess();
        arimaProcess.setArCoefficients(0.2326633241500426, 1.5925411838795802);
        arimaProcess.setMaCoefficients(0.11244125320455554, -0.5199182662567919);
        arimaProcess.setIntegrationOrder(1);
        arimaProcess.setConstant(0.5964750332303466);
        arimaProcess.setVariation(0.13859165428388037);
        double[] observations =
                new double[]{ 0.16815883945913634, 0.8391475554258156, 1.6152684083063231, 2.9942671524900986,
                              5.994230142986245, 9.915810117665863, 16.302331778792237, 25.211430050797915,
                              38.153432580482956, 55.91975542351953, 81.59233570233657, 116.59022349892365,
                              166.45818995777645, 233.9585868610889, 329.74952316786187, 460.3374658618706,
                              643.916286317422, 895.8987493797649, 1247.583750631902, 1731.2358878592256,
                              2404.181805666538, 3331.4765535019237, 4619.75352686623, 6397.095120658891,
                              8862.566112204624, 12267.139259953756, 16986.33416255549, 23506.404791807272,
                              32539.430505167864, 45025.074231369035, 62315.91540432791, 86222.8626276451,
                              119321.7015960958, 165096.0675435309, 228457.9530568175, 316098.08395046135,
                              437396.40842979663, 605189.5575204729, 837402.2339670224, 1158648.1126242036,
                              1603198.517688788, 2218225.9805888776, 3069286.359063292, 4246754.118357274,
                              5876056.260567382, 8130302.165241312, 1.1249514329962187E7, 1.556522003172044E7,
                              2.1536801173093352E7, 2.9799109137647137E7 };
        System.out.println("-----------------");
        System.out.println("Observations: " + Arrays.toString(observations));
        ArimaProcess fittedArimaProcess =
                ArimaFitter.fit(observations, arimaProcess.getArOrder(), arimaProcess.getMaOrder());
        System.out.println("Fitted: " + fittedArimaProcess);
        System.out.println("-----------------");
    }
}
