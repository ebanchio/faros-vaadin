package com.vaadin.faros;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.model.Employee;
import com.vaadin.utils.FarosUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The main view contains a button and a click listener.
 */
@Route("")
@CssImport(value = "./styles/vaadin-grid-styles.css", themeFor = "vaadin-grid")
public class MainView extends VerticalLayout {

    /**
     * Barra superior
     */
    Div headerNavBar;

    /**
     * Indicar de sección
     */
    VerticalLayout sectionLayout;
    List<String> sectionRouteInfo;
    String sectionTitle;
    String sectionDescription;

    /**
     * Layout con contenido de la sección
     */
    EmployeeTable employeeTable;

    /**
     * Layout con contenido de un empleado
     */
    EmployeeDetail employeeDetail;

    private SplitLayout contentSplitLayout;


    public MainView() {
        setMargin(false);
        setPadding(false);
        setSpacing(false);
        setSizeFull();

        sectionRouteInfo = List.of("Admin settings", "Organization setup", "Employees Page");

        initView();
        loadData();
    }

    /**
     * Carga información de empleados
     */
    private void loadData() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Intenta leer el archivo JSON desde el directorio de resources
        try (InputStream inputStream = MainView.class.getResourceAsStream("/employees.json")) {
            if (inputStream == null) {
                System.out.println("Archivo JSON no encontrado.");
                return;
            }

            // Parsear el archivo JSON a un JsonNode
            JsonNode jsonNode = objectMapper.readTree(inputStream);

            Iterator<JsonNode> empleadosJson = jsonNode.get("data").get("employees").elements();

            List<Employee> empleados = new ArrayList<>();

            empleadosJson.forEachRemaining(emp -> {

                // Identity
                JsonNode identityNode = emp.get("identity");
                Employee.Identity identity = null;
                if (identityNode != null) {

                    // Cuentas conectadas
                    Iterator<JsonNode> vcsNodeElements = identityNode.get("vcsUsers").elements();
                    ArrayList<Employee.ConnectedAccounts> connectedAccounts = new ArrayList<>();

                    vcsNodeElements.forEachRemaining(vcsNode -> {
                            if (vcsNode != null) {
                                connectedAccounts.add(new Employee.ConnectedAccounts(FarosUtils.coalesce(vcsNode.get("vcsUser").get("id")),
                                        FarosUtils.coalesce(vcsNode.get("vcsUser").get("source"))));
                            }
                    });

                    identity = new Employee.Identity(FarosUtils.coalesce(identityNode.get("id")),
                            FarosUtils.coalesce(identityNode.get("fullName")), FarosUtils.coalesce(identityNode.get("primaryEmail")),
                            FarosUtils.coalesce(identityNode.get("photoUrl")), connectedAccounts,
                            new ArrayList<>(), new ArrayList<>());
                }

                // Teams
                JsonNode teamsNode = emp.get("teams");
                ArrayList<Employee.Team> teams = new ArrayList<>();

                if (teamsNode != null) {

                    Iterator<JsonNode> teamsElements = teamsNode.elements();

                    teamsElements.forEachRemaining(teamNode -> {
                        if (teamNode != null && teamNode.get("team") != null) {
                            teams.add(new Employee.Team(FarosUtils.coalesce(teamNode.get("team").get("id")),
                                    FarosUtils.coalesce(teamNode.get("team").get("uid")),
                                    FarosUtils.coalesce(teamNode.get("team").get("name")),
                                    FarosUtils.coalesce(teamNode.get("team").get("color"))));
                        }
                    });
                }

                empleados.add(new Employee(
                        FarosUtils.coalesce(emp.get("id")), FarosUtils.coalesce(emp.get("uid")),
                        emp.get("inactive").asBoolean(false), identity, teams));
            });
            if (employeeTable != null) employeeTable.loadData(empleados);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicializa el contenido de la vista
     */
    private void initView() {
        initHeaderNavBar();
        initSectionLayout();
        initEmployeeDetail();

        contentSplitLayout = new SplitLayout(sectionLayout, employeeDetail, SplitLayout.Orientation.HORIZONTAL);
        contentSplitLayout.addClassName("sectionContentLayout");
        contentSplitLayout.setSplitterPosition(70);

        add(headerNavBar, contentSplitLayout);
    }

    private void initHeaderNavBar() {
        headerNavBar = new Div("Header nav bar");
        headerNavBar.addClassName("headerNavBar");
    }

    private void initSectionLayout() {
        sectionLayout = new VerticalLayout();
        sectionLayout.addClassName("sectionLayout");

        if (sectionRouteInfo != null && !sectionRouteInfo.isEmpty()) {
            FontAwesome.Solid.Icon helpIcon = FontAwesome.Solid.FILE_CIRCLE_QUESTION.create();
            helpIcon.setSize("16px");

            HorizontalLayout routeRowLayout = new HorizontalLayout();
            HorizontalLayout route = new HorizontalLayout();

            Button newEmployeeButton = new Button();
            newEmployeeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            newEmployeeButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
            newEmployeeButton.setPrefixComponent(FontAwesome.Solid.PLUS.create());
            newEmployeeButton.setText("New");
            newEmployeeButton.addClickListener(ev -> Notification.show("Add employee"));
            routeRowLayout.setFlexGrow(1, route);

            route.add(new Div(String.join(" > ", sectionRouteInfo)), helpIcon, newEmployeeButton);
            route.addClassName("sectionRoute");
            route.setAlignItems(Alignment.CENTER);

            routeRowLayout.add(route, newEmployeeButton);
            routeRowLayout.setWidthFull();
            sectionLayout.add(routeRowLayout);
        }

        initEmployeeTable();
        sectionLayout.add(employeeTable);
    }

    /**
     * Inicializa el layout para mostrar la grilla de empleados
     */
    private void initEmployeeTable() {
        employeeTable = new EmployeeTable(this);
        employeeTable.addClassName("employeeTable");
    }

    /**
     * Inicializa el layout para mostrar el detalle de un empleado
     */
    private void initEmployeeDetail() {
        employeeDetail = new EmployeeDetail();
        employeeDetail.getStyle().set("display", "none");
    }

    public void mostrarEmpleado(Employee e) {
        employeeDetail.show(e);
    }
}
