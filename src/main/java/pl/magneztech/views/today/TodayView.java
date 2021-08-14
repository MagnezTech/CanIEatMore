package pl.magneztech.views.today;

import java.util.Optional;

import pl.magneztech.data.entity.Record;
import pl.magneztech.data.service.RecordService;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import pl.magneztech.views.MainLayout;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.component.textfield.TextField;

@PageTitle("Today")
@Route(value = "today/:recordID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class TodayView extends Div implements BeforeEnterObserver {

    private final String RECORD_ID = "recordID";
    private final String RECORD_EDIT_ROUTE_TEMPLATE = "today/%d/edit";

    private Grid<Record> grid = new Grid<>(Record.class, false);

    private TextField entry;
    private TextField weight;
    private DatePicker date;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Record> binder;

    private Record record;

    private RecordService recordService;

    public TodayView(@Autowired RecordService recordService) {
        addClassNames("today-view", "flex", "flex-col", "h-full");
        this.recordService = recordService;
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("entry").setAutoWidth(true);
        grid.addColumn("weight").setAutoWidth(true);
        grid.addColumn("date").setAutoWidth(true);
        grid.setDataProvider(new CrudServiceDataProvider<>(recordService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(RECORD_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(TodayView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Record.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.forField(weight).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("weight");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.record == null) {
                    this.record = new Record();
                }
                binder.writeBean(this.record);

                recordService.update(this.record);
                clearForm();
                refreshGrid();
                Notification.show("Record details stored.");
                UI.getCurrent().navigate(TodayView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the record details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> recordId = event.getRouteParameters().getInteger(RECORD_ID);
        if (recordId.isPresent()) {
            Optional<Record> recordFromBackend = recordService.get(recordId.get());
            if (recordFromBackend.isPresent()) {
                populateForm(recordFromBackend.get());
            } else {
                Notification.show(String.format("The requested record was not found, ID = %d", recordId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(TodayView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        entry = new TextField("Entry");
        weight = new TextField("Weight");
        date = new DatePicker("Date");
        Component[] fields = new Component[]{entry, weight, date};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Record value) {
        this.record = value;
        binder.readBean(this.record);

    }
}
