package service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import model.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by mac on 2017/4/7.
 */
public class FfsService {

    public boolean GenerateViolation() {
        final Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        List<Violation> violations = Violation.dao.find("select * from violation where " +
                "ViolationYear != \"\" order by" +
                " Symbol");
        for (Violation violation : violations
                ) {
            Set<Integer> set;
            if (!map.containsKey(Integer.parseInt(violation.getSymbol()))) {
                set = new TreeSet<Integer>();
                for (String year : violation.getViolationYear().split(",")
                        ) {
                    set.add(Integer.parseInt(year));
                }
            } else {
                set = map.get(Integer.parseInt(violation.getSymbol()));
                for (String year : violation.getViolationYear().split(",")
                        ) {
                    set.add(Integer.parseInt(year.trim()));
                }
            }
            map.put(Integer.parseInt(violation.getSymbol()), set);
        }
        boolean succeed = Db.tx(new IAtom() {
            public boolean run() throws SQLException {
                if (Db.update("truncate table use_violation") != 0) {
                    return false;
                }
                for (Integer symbol : map.keySet()
                        ) {
                    for (Integer year : map.get(symbol)
                            ) {
                        UseViolation useViolation = new UseViolation();
                        useViolation.setSymbol(symbol.intValue());
                        useViolation.setYear(year.intValue());
                        if (!useViolation.save()) {
                            return false;
                        }
                    }
                }
                return true;
            }
        });
        return succeed;
    }


    public boolean GenerateIndicator() {
        Db.update("truncate table indicator");
        List<Combas> combasList = Combas.dao.find("select * from combas where Accper like " +
                "'%12-31' and Typrep='A'");
        for (Combas combas : combasList
                ) {
            Combas beforeCombas = Combas.dao.findFirst("select * from combas where " +
                    "Accper='" + (Integer.parseInt(combas.getAccper().toString().substring(0, 4))
                    - 1) + "-12-31'" +
                    " and Typrep='A' and Stkcd=?", combas.getStkcd());
            Profit profit = Profit.dao.findFirst("select * from profit where Stkcd=? and " +
                            "Accper=? and Typrep='A'", combas.getStkcd()
                    , combas.getAccper());
            Profit beforeProfit = Profit.dao.findFirst("select * from profit where " +
                    "Accper='" + (Integer.parseInt(combas.getAccper().toString().substring(0, 4))
                    - 1) + "-12-31'" +
                    " and Typrep='A' and Stkcd=?", combas.getStkcd());
            if (beforeCombas == null || beforeProfit == null || profit.getB001101000().equals("")
                    || profit
                    .getB002000000().equals("") || profit.getB001101000().equals("") || combas
                    .getA001111000
                    ().equals("") || profit.getB001101000().equals("") || combas.getA001111000()
                    .equals("") || beforeCombas
                    .getA001111000().equals("") || profit.getB001101000().equals("") || profit
                    .getB001201000().equals("") ||
                    beforeProfit.getB001101000().equals("") || beforeProfit.getB001201000()
                    .equals("") || profit
                    .getB001101000().equals("") || beforeProfit.getB001101000().equals("") ||
                    combas.getA001123000().equals("") ||
                    profit.getB001101000().equals("") || profit.getB001201000().equals("") ||
                    profit.getB002000000().equals("") ||
                    profit.getB002100000().equals("") || profit.getB001211000().equals("") ||
                    combas.getA001100000().equals("") ||
                    combas.getA002100000().equals("") || profit.getB002000000().equals("") ||
                    profit.getB002000000().equals("") ||
                    combas.getA001100000().equals("") || combas.getA002100000().equals("") ||
                    combas.getA001100000().equals("") ||
                    combas.getA002100000().equals("") || combas.getA001100000().equals("") ||
                    combas.getA001123000().equals("") ||
                    combas.getA002100000().equals("") || profit.getB001101000().equals("") ||
                    profit.getB001201000().equals(""))
                continue;
            ;
            Indicator indicator = new Indicator();
            indicator.setSymbol(combas.getStkcd());
            indicator.setYear(Integer.valueOf(combas.getAccper().toString().substring(0, 4)));
            indicator.setDEBTEQ(calculateDEBTEQ(combas));
            indicator.setSALTA(calculateSALTA(profit, combas));
            indicator.setNPSAL(calculateNPSAL(profit));
            indicator.setRECSAL(calculateRECSAL(combas, profit));
            indicator.setNFATA(calculateNFATA(combas));
            indicator.setRETREND(calculateRETREND(combas, beforeCombas));
            indicator.setGMTREND(calculateGMTREND(profit, beforeProfit));
            indicator.setSALGRTH(calculateSALGRTH(profit, beforeProfit));
            indicator.setINVTA(calculateINVTA(combas));
            indicator.setCASHTA(calculateCASHTA(combas));
            indicator.setLTA(calculateLTA(combas));
            indicator.setLOGDEBT(calculateLOGDEBT(combas));
            indicator.setCOSAL(calculateCOSAL(profit));
            indicator.setEBIT(calculateEBIT(profit));
            indicator.setWCAP(calculateWCAP(combas));
            indicator.setNIFA(calculateNIFA(profit, combas));
            indicator.setTDTA(calculateCASHTA(combas));
            indicator.setNPTA(calculateNPTA(profit, combas));
            indicator.setCACL(calculateCACL(combas));
            indicator.setWCTA(calculateWCTA(combas));
            indicator.setQACL(calculateQACL(combas));
            indicator.setGPTA(calculateGPTA(profit, combas));
            indicator.setLTDTA(calculateLTDTA(combas));
            String[] parameters = {"DEBTEQ", "SALTA", "NPSAL", "RECSAL", "NFATA", "RETREND",
                    "GMTREND", "SALGRTH", "INVTA", "CASHTA", "LTA", "LOGDEBT", "COSAL", "EBIT",
                    "WCAP",
                    "NIFA", "TDTA", "NPTA", "CACL", "WCTA", "QACL", "GPTA", "LTDTA"};
            boolean flag = false;
            for (String para : parameters
                    ) {
                //System.out.println(para + ":" + indicator.getDouble(para));
                if (indicator.getDouble(para).isNaN()) {
                    flag = true;
                    break;
                }
            }
            if (flag)
                continue;
            indicator.save();
        }
        return false;
    }

    //DEBTEQ  	负债/净资产	        X1=Combas.A002000000/Combas.A003000000
    private double calculateDEBTEQ(Combas combas) {
        double A002000000 = combas.getA002000000();
        float A003000000 = combas.getA003000000();
        return A002000000 / A003000000;
    }

    //SALTA   	销售收入/总资产	        X2=profit.B001101000/Combas.A001000000
    private double calculateSALTA(Profit profit, Combas combas) {
        double B001101000 = Double.parseDouble(profit.getB001101000());
        float A001000000 = combas.getA001000000();
        return B001101000 / A001000000;
    }

    //NPSAL   	净利润/销售收入	        X3=profit.B002000000/profit.B001101000
    private double calculateNPSAL(Profit profit) {
        double B002000000 = Double.parseDouble(profit.getB002000000());
        double B001101000 = Double.parseDouble(profit.getB001101000());
        return B002000000 / B001101000;
    }

    //RECSAL  	应收帐款净额/销售收入	X4=Combas.A001111000/profit.B001101000
    private double calculateRECSAL(Combas combas, Profit profit) {
        double A001111000 = Double.parseDouble(combas.getA001111000());
        double B001101000 = Double.parseDouble(profit.getB001101000());
        return A001111000 / B001101000;
    }

    //NFATA   	固定资产净值/总资产	X5=Combas.A001212000/Combas.A001000000
    private double calculateNFATA(Combas combas) {
        double A001212000 = combas.getA001212000();
        double A001000000 = combas.getA001000000();
        return A001212000 / A001000000;
    }

    //RETREND 	应收帐款/连续两年应收帐款X6=Combas.A001111000/连续两年Combas.A001111000
    private double calculateRETREND(Combas combas, Combas beforeCombas) {
        double A001111000 = Double.parseDouble(combas.getA001111000());
        double before = Double.parseDouble(beforeCombas.getA001111000());
        double count = before + A001111000;
        return A001111000 / count;
    }

    //GMTREND 	销售毛利/连续两年销售毛利X7=(profit.B001101000-profit.B001201000)/连续两年(profit
    // .B001101000-profit.B001201000)
    private double calculateGMTREND(Profit profit, Profit beforeProfit) {
        double B001101000 = Double.parseDouble(profit.getB001101000());
        double B001201000 = Double.parseDouble(profit.getB001201000());
        double beforeB001101000 = Double.parseDouble(beforeProfit.getB001101000());
        double beforeB001201000 = Double.parseDouble(beforeProfit.getB001201000());
        return (B001101000 - B001201000) / ((B001101000 - B001201000) +
                (beforeB001101000 - beforeB001201000));
    }

    //SALGRTH 	销售增长率	X8=本年的profit.B001101000/上年的profit.B001101000 -1
    private double calculateSALGRTH(Profit profit, Profit beforeProfit) {
        double B001101000 = Double.parseDouble(profit.getB001101000());
        double before = Double.parseDouble(beforeProfit.getB001101000());
        return B001101000 / before - 1;
    }

    //INVTA   	存货/总资产	X9=Combas.A001123000/Combas.A001000000
    private double calculateINVTA(Combas combas) {
        double A001123000 = Double.parseDouble(combas.getA001123000());
        double A001000000 = combas.getA001000000();
        return A001123000 / A001000000;
    }

    //CASHTA  	现金/总资产	X10=Combas.A001101000/Combas.A001000000
    private double calculateCASHTA(Combas combas) {
        double A001101000 = combas.getA001101000();
        double A001000000 = combas.getA001000000();
        return A001101000 / A001000000;
    }

    //LTA     	总资产对数	X11=log(Combas.A001000000)
    private double calculateLTA(Combas combas) {
        double A001000000 = Math.log(combas.getA001000000());
        return A001000000;
    }

    //LOGDEBT 	总负债对数	X12=log(Combas.A002000000)
    private double calculateLOGDEBT(Combas combas) {
        double A002000000 = Math.log(combas.getA002000000());
        return A002000000;
    }

    //COSAL    	销售毛利率	X13=(profit.B001101000-profit.B001201000)/profit.B001101000
    private double calculateCOSAL(Profit profit) {
        double B001101000 = Double.parseDouble(profit.getB001101000());
        double B001201000 = Double.parseDouble(profit.getB001201000());
        return (B001101000 - B001201000) / B001101000;
    }

    //EBIT    	息税前利润	X14=profit.B002000000+profit.B002100000+profit.B001211000
    private double calculateEBIT(Profit profit) {
        double B002000000 = Double.parseDouble(profit.getB002000000());
        double B002100000 = Double.parseDouble(profit.getB002100000());
        double B001211000 = Double.parseDouble(profit.getB001211000());
        return B002000000 + B002100000 + B001211000;
    }

    //WCAP    	营运资本	X15=combas.A001100000-combas.A002100000
    private double calculateWCAP(Combas combas) {
        double A001100000 = Double.parseDouble(combas.getA001100000());
        double A002100000 = Double.parseDouble(combas.getA002100000());
        return A001100000 - A002100000;
    }

    //NIFA    	净利润/固定资产	X16=profit.B002000000/combas.A001212000
    private double calculateNIFA(Profit profit, Combas combas) {
        double B002000000 = Double.parseDouble(profit.getB002000000());
        double A001212000 = combas.getA001212000();
        return B002000000 / A001212000;
    }

    //TDTA    	资产负债率	X17=Combas.A002000000/Combas.A001000000
    private double calculateTDTA(Combas combas) {
        double A002000000 = combas.getA002000000();
        double A001000000 = combas.getA001000000();
        return A002000000 / A001000000;
    }

    //NPTA    	净利润/总资产	X18=profit.B002000000/Combas.A001000000
    private double calculateNPTA(Profit profit, Combas combas) {
        double B002000000 = Double.parseDouble(profit.getB002000000());
        double A001000000 = combas.getA001000000();
        return B002000000 / A001000000;
    }

    //CACL    	流动资产/流动负债X19=combas.A001100000/combas.A002100000
    private double calculateCACL(Combas combas) {
        double A001100000 = Double.parseDouble(combas.getA001100000());
        double A002100000 = Double.parseDouble(combas.getA002100000());
        return A001100000 / A002100000;
    }

    //WCTA    	营运资本/总资产	X20=(combas.A001100000-combas.A002100000)/Combas.A001000000
    private double calculateWCTA(Combas combas) {
        double A001100000 = Double.parseDouble(combas.getA001100000());
        double A002100000 = Double.parseDouble(combas.getA002100000());
        double A001000000 = combas.getA001000000();
        return (A001100000 - A002100000) / A001000000;
    }

    //QACL    	速动资产/流动负债X21=(combas.A001100000-combas.A001123000 )/combas.A002100000
    private double calculateQACL(Combas combas) {
        double A001100000 = Double.parseDouble(combas.getA001100000());
        double A001123000 = Double.parseDouble(combas.getA001123000());
        double A002100000 = Double.parseDouble(combas.getA002100000());
        return (A001100000 - A001123000) / A002100000;
    }

    //GPTA    	销售毛利/总资产	X22=(profit.B001101000-profit.B001201000)/Combas.A001000000
    private double calculateGPTA(Profit profit, Combas combas) {
        double B001101000 = Double.parseDouble(profit.getB001101000());
        double B001201000 = Double.parseDouble(profit.getB001201000());
        double A001000000 = combas.getA001000000();
        return (B001101000 - B001201000) / A001000000;
    }

    //LTDTA   	长期负债/总资产	X23=combas.A002206000/Combas.A001000000
    private double calculateLTDTA(Combas combas) {
        double A002206000 = combas.getA002206000();
        double A001000000 = combas.getA001000000();
        return A002206000 / A001000000;
    }
}
