package com.vaadin.faros;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.componentfactory.Popup;
import com.vaadin.componentfactory.PopupPosition;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.model.Employee;
import com.vaadin.model.EmployeeFilter;
import com.vaadin.utils.FarosUtils;
import org.vaadin.klaudeta.PaginatedGrid;

import java.util.*;
import java.util.function.Consumer;
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

    /**
     * Mantiene los filtros que se están aplicando sobre los empleados
     */
    private EmployeeFilter currentFilter = new EmployeeFilter();

    /* Mantengo posibles valores para filtros */
    private Set<String> availableTeams = new HashSet<>();
    private Set<String> availableAccountsConnected = new HashSet<>();
    private Set<String> availableTrackingStatus = new HashSet<>();

    public enum FilterType {
        ACCOUNTS_CONNECTED("Accounts Connected"),
        TEAM("Team"),
        TRACKING_STATUS("Tracking status");

        private final String label;

        FilterType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }


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

        grid.addComponentColumn(e -> getNameColumn(e)).setHeader("Name").setSortable(true).setWidth("calc((100% - 110px) * 0.3)");
        grid.addComponentColumn(e -> getStatusColumn(e)).setHeader("Tracking Status").setSortable(true).setWidth("calc((100% - 110px) * 0.2)");
        grid.addComponentColumn(e -> getTeamsColumn(e)).setHeader("Teams").setWidth("calc((100% - 110px) * 0.3)");
        grid.addComponentColumn(e -> getConnectedAccountsColumn(e)).setHeader("Accounts Connected").setWidth("calc((100% - 110px) * 0.2)");
        grid.addComponentColumn(e -> new Button("View", ev -> mostrarEmpleado(e))).setHeader("").setWidth("110px");

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
            badge = new Div(team.getName());
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

    /**
     * Agrega el layout con opciones para filtrar empleados
     */
    private void addFiltersField() {

        //Filtrar por nombre
        txtFieldNameFilter = new TextField();
        txtFieldNameFilter.setPlaceholder("Search employees by name...");
        txtFieldNameFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        txtFieldNameFilter.setWidthFull();
        txtFieldNameFilter.setValueChangeMode(ValueChangeMode.EAGER);

        txtFieldNameFilter.addValueChangeListener(event -> {
            filterByName(event.getValue().toLowerCase());
        });
        add(txtFieldNameFilter);

        //Resto de los filtros

        HorizontalLayout filtersLayout = new HorizontalLayout();
        filtersLayout.setPadding(false);

        // Botón para abrir el menú de filtros
        addMainFilterButton(filtersLayout);

        add(filtersLayout);
    }

    /**
     * Agrega el botón para filtrar por propiedades de los empleados
     * @param container
     */
    private void addMainFilterButton(HorizontalLayout container) {

        // Crear un botón para abrir el menú de filtros
        Button filterButton = new Button();
        filterButton.setPrefixComponent(FontAwesome.Solid.PLUS.create());
        filterButton.setText("Add Filter");

        Popup popupFilters = new Popup();
        popupFilters.setPosition(PopupPosition.BOTTOM);

        // Crear una Grid con selección múltiple
        Div filterGridLabel = new Div("FILTER BY");
        filterGridLabel.addClassName("filterGridLabel");

        Grid<FilterType> filterGrid = new Grid<>();
        filterGrid.setItems(FilterType.values());
        filterGrid.addColumn(item -> item.getLabel());
        filterGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        filterGrid.addClassName("filterGrid");
        filterGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        filterGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);

        // Set para almacenar los filtros seleccionados
        Set<FilterType> selectedFilters = new HashSet<>();

        // Listener para la selección de múltiples filtros
        filterGrid.addSelectionListener(event -> {
            selectedFilters.clear();
            selectedFilters.addAll(event.getAllSelectedItems());
        });

        HorizontalLayout activeFiltersLayout = new HorizontalLayout();
        activeFiltersLayout.setPadding(false);
        activeFiltersLayout.setAlignItems(Alignment.CENTER);

        // Botones Apply y Cancel
        Button applyButton = new Button("Apply", event -> {
            setFilters(filterGrid, activeFiltersLayout, selectedFilters);
            popupFilters.hide();
        });
        applyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", event -> popupFilters.hide());

        // Layout para los botones
        HorizontalLayout buttonLayout = new HorizontalLayout(applyButton, cancelButton);
        buttonLayout.addClassName("filterButtonLayout");

        // Layout del Dialog
        VerticalLayout dialogLayout = new VerticalLayout(filterGridLabel, filterGrid, buttonLayout);
        dialogLayout.setSpacing(false);
        dialogLayout.setPadding(false);
        popupFilters.add(dialogLayout);

        // Al hacer clic en el botón, se abre el Dialog
        filterButton.addClickListener(event -> {
            if (popupFilters.isOpened()) popupFilters.hide();
            else popupFilters.show();
        });

        // Añadir el botón al layout principal
        long btnId = (new Random()).nextLong();
        filterButton.setId("" + btnId);
        container.add(filterButton);
        popupFilters.setFor("" + btnId);
        container.add(popupFilters);

        // Agrego contenedor de los filtros activos
        container.add(activeFiltersLayout);
    }

    /**
     * Agrega un botón para seleccionar las opciones específicas a considerar para un filtro
     * @param container
     * @param filterBy
     */
    private void addSecondaryFilterButton(HorizontalLayout container,
                                          FilterType filterBy, Set<String> options, String filterPlaceholder,
                                          Runnable onFilterRemoved) {

        // Crear un botón para abrir el menú de filtros
        HorizontalLayout filterButton = new HorizontalLayout();
        filterButton.addClassName("filterSecondaryButton");
        filterButton.setSpacing(false);
        filterButton.setPadding(false);

        // Inicializa el popup con opciones para este filtro particular
        Popup popupFilters = new Popup();
        popupFilters.setPosition(PopupPosition.BOTTOM);

        FontAwesome.Solid.Icon removeFilterButton = FontAwesome.Solid.XMARK.create();
        removeFilterButton.getElement().addEventListener("click", ev -> {
            container.remove(filterButton);
            container.remove(popupFilters);
            onFilterRemoved.run();
        }).addEventData("event.stopPropagation()");
        filterButton.add(new Div(filterBy.getLabel()), removeFilterButton);

        // Crear una Grid con selección múltiple
        Grid<String> filterGrid = new Grid<>();
        filterGrid.setItems(options);
        filterGrid.addColumn(item -> item);
        filterGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        filterGrid.addClassName("filterGrid");
        filterGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        filterGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        filterGrid.addThemeVariants(GridVariant.LUMO_COMPACT);


        TextField txtFilterOptions = new TextField();
        txtFilterOptions.setPlaceholder(filterPlaceholder);
        txtFilterOptions.setPrefixComponent(VaadinIcon.SEARCH.create());
        txtFilterOptions.addClassName("filterGridTextField");
        txtFilterOptions.setValueChangeMode(ValueChangeMode.EAGER);
        txtFilterOptions.addValueChangeListener(event ->
                filterGrid.setItems(options.stream().filter(op -> op.toLowerCase().
                        contains(event.getValue().toLowerCase())).collect(Collectors.toList())));


        Div selectAllOption = new Div("Select all");
        selectAllOption.addClassName("filterGridSelectAll");
        selectAllOption.addClickListener(ev -> filterGrid.asMultiSelect().select(options));

        // Set para almacenar los filtros seleccionados
        Set<String> selectedFilters = new HashSet<>();

        // Listener para la selección de múltiples filtros
        filterGrid.addSelectionListener(event -> {
            selectedFilters.clear();
            selectedFilters.addAll(event.getAllSelectedItems());
        });

        // Botones Apply y Cancel
        Button applyButton = new Button("Apply", event -> {
            filterBy(filterBy, selectedFilters);
            popupFilters.hide();
        });
        applyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", event -> popupFilters.hide());

        // Layout para los botones
        HorizontalLayout buttonLayout = new HorizontalLayout(applyButton, cancelButton);
        buttonLayout.addClassName("filterButtonLayout");

        // Layout del Dialog
        VerticalLayout dialogLayout = new VerticalLayout(txtFilterOptions, selectAllOption, filterGrid, buttonLayout);
        dialogLayout.setSpacing(false);
        dialogLayout.setPadding(false);
        popupFilters.add(dialogLayout);

        // Al hacer clic en el botón, se abre el Dialog
        filterButton.addClickListener(event -> {
            if (popupFilters.isOpened()) popupFilters.hide();
            else {
                popupFilters.show();
                filterGrid.asMultiSelect().select();
            }
        });

        // Añadir el botón al layout principal
        long btnId = (new Random()).nextLong();
        filterButton.setId("" + btnId);
        container.add(filterButton);
        popupFilters.setFor("" + btnId);
        container.add(popupFilters);
    }

    private void setFilters(Grid<FilterType> filterGrid, HorizontalLayout layout, Set<FilterType> selectedFilters) {
        layout.removeAll();

        // Actualizar currentFilter
        if (!selectedFilters.contains(FilterType.TEAM)) currentFilter.setTeams(Set.of());
        if (!selectedFilters.contains(FilterType.ACCOUNTS_CONNECTED)) currentFilter.setAccountsConnectedNames(Set.of());
        if (!selectedFilters.contains(FilterType.TRACKING_STATUS)) currentFilter.setTrackingStatus(Set.of());

        for (FilterType selectedFilter : selectedFilters) {
            switch (selectedFilter) {
                case TEAM:
                    addSecondaryFilterButton(layout, selectedFilter,
                            availableTeams, "Search team name...", () -> {
                        filterGrid.deselect(FilterType.TEAM);
                        currentFilter.setTeams(Set.of());
                        grid.setItems(currentFilter.apply(empleados));
                    });
                    break;
                case ACCOUNTS_CONNECTED:
                    addSecondaryFilterButton(layout, selectedFilter,
                            availableAccountsConnected, "Search connected account name...", () -> {
                                filterGrid.deselect(FilterType.ACCOUNTS_CONNECTED);
                                currentFilter.setAccountsConnectedNames(Set.of());
                                grid.setItems(currentFilter.apply(empleados));
                            });
                    break;
                case TRACKING_STATUS: {
                    addSecondaryFilterButton(layout, selectedFilter,
                            availableTrackingStatus, "Search tracking status...", () -> {
                                filterGrid.deselect(FilterType.TRACKING_STATUS);
                                currentFilter.setTrackingStatus(Set.of());
                                grid.setItems(currentFilter.apply(empleados));
                            });
                    break;
                }
            }
        }

        grid.setItems(currentFilter.apply(empleados));
    }

    public void loadData(List<Employee> empleados) {
        this.empleados = empleados;

        availableTeams = empleados.stream()
                .flatMap(empleado -> empleado.getTeams().stream())
                .map(Employee.Team::getName)
                .collect(Collectors.toSet());

        availableAccountsConnected = empleados.stream()
                .flatMap(empleado -> empleado.getIdentity().getConnectedAccounts().stream())
                .map(Employee.ConnectedAccounts::getSource)
                .collect(Collectors.toSet());

        availableTrackingStatus = Set.of("Active", "Inactive");

        grid.setItems(this.empleados);
    }

    /**
     * Dado una cadena de texto filtra por los empleados que contengan ese valor en su nombre
     * @param name
     */
    private void filterByName(String name) {
        currentFilter.setName(name);
        grid.setItems(currentFilter.apply(empleados));
    }

    /**
     * Aplica filtros específicos al listado de empleados
     * @param filterType
     * @param options
     */
    private void filterBy(FilterType filterType, Set<String> options) {

        switch (filterType) {
            case TEAM: {
                currentFilter.setTeams(options);
                break;
            }
            case ACCOUNTS_CONNECTED: {
                currentFilter.setAccountsConnectedNames(options);
                break;
            }
            case TRACKING_STATUS: {
                currentFilter.setTrackingStatus(options);
                break;
            }
        }

        grid.setItems(currentFilter.apply(empleados));
    }

}
