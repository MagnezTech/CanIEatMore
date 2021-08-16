package pl.magneztech.views.calendar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;
import pl.magneztech.data.entity.Entry;
import pl.magneztech.data.entity.Record;
import pl.magneztech.data.service.DayRecordCrudService;
import pl.magneztech.data.service.EntryService;
import pl.magneztech.data.service.RecordService;
import pl.magneztech.views.MainLayout;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@PageTitle("Calendar")
@Route(value = "calendar/:recordID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class CalendarView extends Div implements BeforeEnterObserver {

    private final String RECORD_ID = "recordID";
    private final String RECORD_EDIT_ROUTE_TEMPLATE = "calendar/%d/edit";
    private final DecimalFormat df = new DecimalFormat("###.###");
    private final Grid<Record> grid = new Grid<>(Record.class, false);
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final BeanValidationBinder<Record> binder;
    private final RecordService recordService;
    private final EntryService entryService;
    private final DayRecordCrudService dayRecordCrudService;
    private final HeaderRow sumRow;
    private ComboBox<Entry> entriesCombobox;
    private TextField weight;
    private DatePicker datePicker = new DatePicker();
    private Record record;

    public CalendarView(@Autowired RecordService recordService, @Autowired EntryService entryService) {
        addClassNames("calendar-view", "flex", "flex-col", "h-full");
        this.recordService = recordService;
        this.entryService = entryService;
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        dayRecordCrudService = new DayRecordCrudService(recordService, LocalDate.now());

        // Configure Grid
        grid.addColumn(record -> record.getEntry().getName()).setAutoWidth(true).setHeader("Entry").setSortable(true)
                .setComparator(Comparator.comparing(o -> o.getEntry().getName())).setKey("entry");
        grid.addColumn("weight").setAutoWidth(true);
        grid.addColumn(record -> df.format(record.kcal())).setAutoWidth(true).setHeader("kcal").setSortable(true).setKey("kcal");
        grid.addColumn(record -> df.format(record.fat())).setAutoWidth(true).setHeader("Fat").setSortable(true).setKey("fat");
        grid.addColumn(record -> df.format(record.carbohydrate())).setAutoWidth(true).setHeader("Carbohydrate").setSortable(true).setKey("carbohydrate");
        grid.addColumn(record -> df.format(record.protein())).setAutoWidth(true).setHeader("Protein").setSortable(true).setKey("protein");
        grid.addComponentColumn(r -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH));
            button.addClickListener(event -> {
                dayRecordCrudService.delete(r.getId());
                refreshGrid();
            });
            return button;
        }).setAutoWidth(true).setHeader("");
        grid.setDataProvider(new CrudServiceDataProvider<>(dayRecordCrudService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        sumRow = grid.appendHeaderRow();

        calculateSum();


        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(RECORD_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CalendarView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Record.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.forField(weight).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("weight");
        binder.forField(entriesCombobox).bind("entry");

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
                this.record.setDate(datePicker.getValue());

                recordService.update(this.record);
                clearForm();
                refreshGrid();
                Notification.show("Record details stored.");
                UI.getCurrent().navigate(CalendarView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the record details.");
            }
        });

        datePicker.setValue(LocalDate.now());
        datePicker.addClassName("centerAll");
        datePicker.addValueChangeListener(event -> {
            datePicker.setValue(event.getValue());
            dayRecordCrudService.setDate(event.getValue());
            clearForm();
            refreshGrid();
        });
    }

    private void calculateSum() {
        List<Record> records = dayRecordCrudService.findAll();
        sumRow.getCell(grid.getColumnByKey("entry")).setText("Summary");
        sumRow.getCell(grid.getColumnByKey("weight"))
                .setText(df.format(records.stream().mapToDouble(Record::getWeight).sum()));
        sumRow.getCell(grid.getColumnByKey("kcal"))
                .setText(df.format(records.stream().mapToDouble(Record::kcal).sum()));
        sumRow.getCell(grid.getColumnByKey("fat"))
                .setText(df.format(records.stream().mapToDouble(Record::fat).sum()));
        sumRow.getCell(grid.getColumnByKey("carbohydrate"))
                .setText(df.format(records.stream().mapToDouble(Record::carbohydrate).sum()));
        sumRow.getCell(grid.getColumnByKey("protein"))
                .setText(df.format(records.stream().mapToDouble(Record::protein).sum()));
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
                event.forwardTo(CalendarView.class);
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
        entriesCombobox = new ComboBox<>("Entry", entryService.getAllEntries());
        entriesCombobox.setItemLabelGenerator(Entry::getName);
        weight = new TextField("Weight");
        Component[] fields = new Component[]{entriesCombobox, weight};

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
        wrapper.addClassName("centerAll");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(datePicker, grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
        calculateSum();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Record value) {
        this.record = value;
        binder.readBean(this.record);
    }
}
