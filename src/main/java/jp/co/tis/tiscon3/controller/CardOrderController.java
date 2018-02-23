package jp.co.tis.tiscon3.controller;

import enkan.component.BeansConverter;
import enkan.component.doma2.DomaProvider;
import enkan.data.HttpResponse;
import jp.co.tis.tiscon3.dao.CardOrderDao;
import jp.co.tis.tiscon3.entity.CardOrder;
import jp.co.tis.tiscon3.form.CardOrderForm;
import jp.co.tis.tiscon3.form.CardOrderForm2;
import kotowari.component.TemplateEngine;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.ArrayList;

import static enkan.util.HttpResponseUtils.RedirectStatusCode.SEE_OTHER;
import static kotowari.routing.UrlRewriter.redirect;

/**
 * カード申し込みに関するcontrollerクラス.
 *
 * @author hirano
 */
public class CardOrderController {

    @Inject
    private TemplateEngine templateEngine;

    @Inject
    private DomaProvider daoProvider;

    @Inject
    private BeansConverter beans;

    private CardOrderDao cardOrderDao;

    private int[] t;

    @PostConstruct
    public void init() {
        cardOrderDao = daoProvider.getDao(CardOrderDao.class);
    }

    /**
     * 本人登録ページを表示します.
     *
     * @return 本人登録ページresponse
     */
    public HttpResponse inputUser() {
        ArrayList<String> continentList = new ArrayList<String>();
        for(int i=0;i<119;i++){
            continentList.add((1900+i)+"");
        }
        return templateEngine.render("cardOrder/user", "form", new CardOrderForm(), "continentList", continentList);
    }

    /**
     * お勤め先登録ページを表示します.
     *
     * @return お勤め先登録ページresponse
     */

    private int syokugyou=1; //職業分岐用の変数
    public void  syokugyou(){
        syokugyou=0;
    }
    public HttpResponse inputJob(CardOrderForm form) {
        // エラーを出したくないので強制的にエラーを消す.

       // CardOrderForm.sub();
        ArrayList<String> continentList = new ArrayList<String>();
        for(int i=0;i<119;i++){
            continentList.add((1900+i)+"");
        }
        if (form.hasErrors()) {
            return templateEngine.render("cardOrder/user", "form", form, "continentList", continentList);
        }
        if(form.getJob().equals("会社員")||form.getJob().equals("経営自営")||form.getJob().equals("契約派遣")||form.getJob().equals("公務員")||form.getJob().equals("民間団体")||form.getJob().equals("他有職")) { //職業の選択で分岐
            return templateEngine.render("cardOrder/job", "form", form, "continentList", continentList);
        }
        return templateEngine.render("cardOrder/completed", "form", form, "continentList", continentList);
    }

    /**
     * 本人登録ページに戻ります.
     *
     * @return 本人登録ページresponse
     */
    public HttpResponse modifyUser(CardOrderForm form) {
        // エラーを出したくないので強制的にエラーを消す.
        form.setErrors(null);
        ArrayList<String> continentList = new ArrayList<String>();
        for(int i=0;i<119;i++){
            continentList.add((1900+i)+"");
        }
        return templateEngine.render("cardOrder/user", "form", form, "continentList", continentList);
    }

    /**
     * カード申し込み情報をDatabaseに登録します.
     *
     * @return 完了ページへのリダイレクトresponse
     */
    @Transactional
    public HttpResponse create(CardOrderForm2 form) {
        ArrayList<String> continentList = new ArrayList<String>();
        for(int i=0;i<121;i++){
            continentList.add((1900+i)+"");
        }
        if (form.hasErrors()) {
            return templateEngine.render("cardOrder/job", "form", form, "continentList", continentList);
        }
        CardOrder cardOrder = beans.createFrom(form, CardOrder.class);

        cardOrderDao.insert(cardOrder);

        return redirect(getClass(), "completed", SEE_OTHER);
    }

    /**
     * 完了ページを表示します.
     *
     * @return 完了ページresponse
     */
    public HttpResponse completed() {
        return templateEngine.render("cardOrder/completed");
    }

}
