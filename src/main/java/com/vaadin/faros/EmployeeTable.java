package com.vaadin.faros;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.model.Employee;
import com.vaadin.utils.FarosUtils;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeTable extends VerticalLayout {

    private static final Map<String, String> connectedAccountsMap = new HashMap<>(){
        {
            put("GitHub", "img/github.png");
            put("Google Calendar", "img/google-calendar.png");
            put("Orderly", "img/orderly.png");
            put("Pager Duty", "img/pager-duty.png");
        }
    };

    private final MainView container;
    private List<Employee> empleados = new ArrayList<>();

    private TextField txtFieldNameFilter;
    private PaginatedGrid<Employee, ?> grid;

    EmployeeTable(MainView container) {
        this.container = container;
        Div title = new Div("Employees");
        title.addClassName("sectionTitle");

        Div descripcion = new Div("Easily assign employees to teams, include them for tracking in team productivity status, and manage their connected accounts.  ");
        descripcion.addClassName("sectionDsecription");

        add(title, descripcion);

        addFiltersField();

        addDataTable();
    }

    private void addDataTable() {

        grid = new PaginatedGrid<>();

        grid.addComponentColumn(e -> getNameColumn(e)).setHeader("Name").setSortable(true).setWidth("25%");
        grid.addComponentColumn(e -> getStatusColumn(e)).setHeader("Tracking Status").setSortable(true).setWidth("15%");
        grid.addComponentColumn(e -> getTeamsColumn(e)).setHeader("Teams").setWidth("25%");
        grid.addComponentColumn(e -> getConnectedAccountsColumn(e)).setHeader("Accounts Connected").setWidth("20%");
        grid.addComponentColumn(e -> new Button("View", ev -> mostrarEmpleado(e))).setHeader("").setWidth("15%");

        // Sets the max number of items to be rendered on the grid for each page
        grid.setPageSize(5);

        // Sets how many pages should be visible on the pagination before and/or after the current selected page
        grid.setPaginatorSize(5);

        add(grid);
    }

    /**
     * Renderiza la columna con info de la identidad del empleado
     * @param e
     * @return
     */
    private HorizontalLayout getNameColumn(Employee e) {
        Div avatar = new Div();

        avatar.getStyle().set("background-image", String.format("url('%s')", e.getIdentity().getPhotoUrl()));
        avatar.addClassName("employeeTableAvatar");

        Div name = new Div(e.getIdentity().getFullName());
        name.addClassName("employeeTableName");

        Div email = new Div(e.getIdentity().getPrimaryEmail());
        email.addClassName("employeeTableEmail");

        VerticalLayout auxLayout = new VerticalLayout(name, email);
        auxLayout.setSpacing(false);

        HorizontalLayout identityLayout = new HorizontalLayout(avatar, auxLayout);
        identityLayout.setAlignItems(Alignment.CENTER);
        identityLayout.setSpacing(false);

        return identityLayout;
    }

    /**
     * Renderiza la columna con info de estado del empleado
     * @param e
     * @return
     */
    private HorizontalLayout getStatusColumn(Employee e) {
        Icon userIcon = FontAwesome.Solid.CIRCLE_USER.create();
        userIcon.addClassName("employeeTableStatusIcon");

        Div included = new Div(e.isActive() ? "Included" : "Not included"); //No pude determinar qué representa este campo
        included.addClassName("employeeTableStatusIncluded");

        Div active = new Div(e.isActive() ? "Active" : "Inactive");
        active.addClassName("employeeTableStatus");

        if (e.isActive()) {
            userIcon.addClassName("active");
            active.addClassName("active");
        }
        else {
            userIcon.addClassName("inactive");
            active.addClassName("inactive");
        }

        VerticalLayout auxLayout = new VerticalLayout(included, active);
        auxLayout.setSpacing(false);

        HorizontalLayout identityLayout = new HorizontalLayout(userIcon, auxLayout);
        identityLayout.setAlignItems(Alignment.CENTER);
        identityLayout.setSpacing(false);

        return identityLayout;
    }

    /**
     * Renderiza la columna con info de equipos
     * @param e
     * @return
     */
    private HorizontalLayout getTeamsColumn(Employee e) {
        HorizontalLayout teamsLayout = new HorizontalLayout();
        teamsLayout.setMargin(false);
        teamsLayout.setPadding(false);

        Div badge;
        for (Employee.Team team : e.getTeams()) {
            badge = new Div(team.getUid());
            badge.addClassName("employeeTableTeamTag");

            String backgroundColor = FarosUtils.empty(team.getColor()) ?
                    FarosUtils.lightenColor(Employee.Team.DEFAULT_TEAM_COLOR, .9) :
                    FarosUtils.lightenColor(team.getColor(), .9);
            String fontColor = FarosUtils.empty(team.getColor()) ?
                    Employee.Team.DEFAULT_TEAM_COLOR : team.getColor();

            badge.getStyle().set("background-color", backgroundColor).set("color", fontColor);

            teamsLayout.add(badge);
        }

        return teamsLayout;
    }

    /**
     * Renderiza la columna con info de cuentas conectadas
     * @param e
     * @return
     */
    private HorizontalLayout getConnectedAccountsColumn(Employee e) {

        HorizontalLayout connectedAccounts = new HorizontalLayout();
        connectedAccounts.setPadding(false);
        connectedAccounts.setSpacing(false);

        for (Employee.ConnectedAccounts ca : e.getIdentity().getConnectedAccounts()) {

            Image caImg;
            FontAwesome.Solid.Icon caNotFoundIcon;

            if (connectedAccountsMap.containsKey(ca.getSource())) {
                caImg = new Image(connectedAccountsMap.get(ca.getSource()), ca.getSource());
                FarosUtils.setTitle(caImg.getElement(), ca.getSource());
                caImg.addClassName("employeeTableConnectedNetwork");
                connectedAccounts.add(caImg);
            }
            else {
                caNotFoundIcon = FontAwesome.Solid.CIRCLE_QUESTION.create();
                FarosUtils.setTitle(caNotFoundIcon.getElement(), ca.getSource());
                caNotFoundIcon.addClassName("employeeTableConnectedNetwork");
                connectedAccounts.add(caNotFoundIcon);
            }
        }

        return connectedAccounts;
    }


    private void mostrarEmpleado(Employee e) {
        container.mostrarEmpleado(e);
    }

    private void addFiltersField() {
        txtFieldNameFilter = new TextField();
        txtFieldNameFilter.setPlaceholder("Search employees by name...");
        txtFieldNameFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        txtFieldNameFilter.setWidthFull();

        txtFieldNameFilter.addValueChangeListener(event -> {
            if (FarosUtils.empty(event.getValue())) grid.setItems(empleados);
            else grid.setItems(empleados.stream().filter(e ->
                            e.getIdentity().getFullName().toLowerCase().contains(event.getValue().toLowerCase()))
                    .collect(Collectors.toList()));
        });
        add(txtFieldNameFilter);
    }

    public void loadData(List<Employee> empleados) {
        this.empleados = empleados;
        grid.setItems(this.empleados);
    }
}
