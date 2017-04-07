package controller;

import com.jfinal.core.Controller;
import service.FfsService;

/**
 * Created by mac on 2017/4/7.
 */
public class FfsController extends Controller {
    private FfsService ffsService;

    public void generateViolations(){
        ffsService = new FfsService();
        boolean succeed = ffsService.GenerateViolation();
        renderJson(succeed);
    }

}
