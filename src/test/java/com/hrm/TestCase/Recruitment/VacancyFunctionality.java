package com.hrm.TestCase.Recruitment;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hrm.Base.TestBase;
import com.hrm.Base.TestUtil;
import com.hrm.Pages.RecruitmentPage.VacanciesPage;
import com.hrm.Pages.RecruitmentPage.VacancyFormPage;
import com.hrm.Util.Log;
import com.hrm.Util.TestDataShare;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

@Epic("Recruitment")
@Feature("Vacancies")
public class VacancyFunctionality extends TestBase {

    @Test(description = "Thêm vacancy mới", priority = 1)
    public void addVacancy() {
        TestUtil.recruitmentUtil();
        VacanciesPage vacanciesPage = new VacanciesPage(driver);
        vacanciesPage.open();
        Assert.assertTrue(vacanciesPage.isOpen(), "Không mở được danh sách Vacancies");

        vacanciesPage.goToAddVacancy();
        VacancyFormPage form = new VacancyFormPage(driver);
        form.addVacancy(TestDataShare.VACANCY_NAME);
        Assert.assertTrue(form.isSaveSuccessful(), "Thêm vacancy không thành công");
        Log.info("Đã thêm vacancy test " + TestDataShare.VACANCY_NAME);
    }

    @Test(description = "Tìm vacancy vừa tạo", dependsOnMethods = "addVacancy", priority = 2)
    public void searchVacancy() {
        TestUtil.recruitmentUtil();
        VacanciesPage vacanciesPage = new VacanciesPage(driver);
        vacanciesPage.open();
        vacanciesPage.search(TestDataShare.VACANCY_NAME);

        Assert.assertTrue(vacanciesPage.hasVacancy(TestDataShare.VACANCY_NAME),
                "Không tìm thấy vacancy " + TestDataShare.VACANCY_NAME);
    }

    @Test(description = "Sửa vacancy vừa tạo", dependsOnMethods = "searchVacancy", priority = 3)
    public void editVacancy() {
        TestUtil.recruitmentUtil();
        VacanciesPage vacanciesPage = new VacanciesPage(driver);
        vacanciesPage.open();
        vacanciesPage.search(TestDataShare.VACANCY_NAME);
        Assert.assertTrue(vacanciesPage.hasVacancy(TestDataShare.VACANCY_NAME),
                "Không tìm thấy vacancy để sửa");

        vacanciesPage.edit(TestDataShare.VACANCY_NAME);
        VacancyFormPage form = new VacancyFormPage(driver);
        form.renameVacancy(TestDataShare.EDITED_VACANCY_NAME);
        Assert.assertTrue(form.isSaveSuccessful(), "Sửa vacancy không thành công");

        vacanciesPage = new VacanciesPage(driver);
        vacanciesPage.search(TestDataShare.EDITED_VACANCY_NAME);
        Assert.assertTrue(vacanciesPage.hasVacancy(TestDataShare.EDITED_VACANCY_NAME),
                "Vacancy sau khi sửa không xuất hiện trong kết quả tìm kiếm");
    }
}
