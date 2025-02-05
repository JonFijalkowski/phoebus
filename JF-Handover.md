# Handover for Jonathan's Placement Project (Phoebus Interactive Graph Widget)
Establishing current state of project, Defining next steps and sharing knowledge gathered.



## **Revised Requirements**
### Must-Do
#### Mouse drag to edit points
*Add the ability to adjust values of PVs on a plot using the mouse. In essence, it will be drag-and-drop behaviour for graph points.
#### "Save Changes" button (Adjusted concept)
*Add a button to apply changes made in the interface to the "Live" PV, so that changes made aren't immediately applied.

#### "Cancel" button*
*Similar to the Save Changes button, this would instead replace the edited PVs values with that of the original copy. It would also be separate to the XYPlot widget.*

### Maybe / Further work
#### Undoing changes
*Add a button which allows the reversion of the most recent (or X most recent) change(s), if a mistake has been made*
#### Axis locking
*Provide a means to "lock" an axis for an edit, so only the X or Y value for a point is changed.*
#### Editing multiple values at the same time
Additional input listeners/detectors could be used, and the stored data of selected points can be made into an array, so a series of updates can be made at one time. Could be more complex due to needing each coordinate to change according to difference in mouse pos, rather than just matching mouse position

### Out of scope / Separate projects
#### Adding new points to a PV
*Add the ability to add new points to a plotted PV trace within Phoebus*

Current Plot system doesn't seem to easily allow for X axis changes during runtime, so it is unlikely this can be done within the current project's limits. In the specific example of datasets, which do not have specific X/Y pairings, and only have Y values correspond to X values/timestamps by sequence position, they would need to have updates made to all traces to allow adding new points in the X axis. (disrupting the sequence order with a new X point would offset all others after it). Considerations would have to be made if an inbetween/interpolated point would be added for every trace in order to preserve order.
#### Interpolation between graph points
*Add the ability to produce additional points inbetween others by different methods, such as linear, polynomial or smoothed curves.*

This would need to be another project entirely, creating scripts to take in a selection of points and produce a new, larger set of points/values. More complex interpolation such as polynomial fit or smoothed curves would be even harder, and likely could be solved with preexisting mathematical packages, such as those available in Python.
#### Scaling all values by some scalar input
*Add the ability to apply some uniform scalar function to all/a selection of points in a PV*
This would need to be another separate button to those proposed, which would apply a flat scalar multiplier to all datapoints in a supplied list PV, then update the original with this. This could be worked into the new "editable" graph functionality, by the script applying the change to the designated Editable Copy, and using the same save/cancel buttons to apply that change to the original.
#### Complex scaling
*Add the ability to apply a mathematical function to the points on a PV graph (some points could be increased more than others)*
Like the interpolation, separating into another project would be necessary. Applying complex mathematical functions to a dataset would need to be implemented using external packages, as implementing both inputting and applying these functions, from scratch, would be needlessly time consuming.
#### Archiving changes
*Store previous values of PVs so that they can be read and restored*

Beyond undoing changes within a session, retaining and restoring old values for PVs would be a much greater, wide-spanning project. This would require additional considerations for where this information would be stored, how it would be stored and kept secure, and how often these backups/archives are made.


## Current state of project

### **What has been done**
#### Requirements gathered
Though the project had a general concept, the specific requirements, goals and scope were not clearly defined, so after communicating with ISIS staff that may use this feature, and
Above is the current list of requirements, some of which have been adjusted in priority as their complexity is greater than was originally estimated. Others have moved to be part of separate work, due to changes in the design.
The "Must" requirements are considered the "core" of the project's goals, and will provide a useful basis for the desired behaviour which can both be used by ISIS staff, and contributed to the Phoebus community for use in other facilities.

#### Design work
Github issue, presenting concept to the Phoebus developer community. General appeal for the goal of the project, so would be welcome if/when completed and contributed back to Phoebus.
Knowledge on system within team was very limited, so was starting from scratch.
Initial ideas were based off limited knowledge, so the scope of the project, the feasibility of requirements, and the implementation to fulfil them changed as knowledge of the codebase grew.

Collaborated with code owner Kay Kasemir for design choices, maintaining best practices, and receiving help with understanding the system.
On-board with the current designs, so it's unlikely they will have to change, unless unforseen practical limitations affect implementation.

#### Edit Mode
A new button has been added to `XYPlot` toolbar, which can be toggled like the other modes, and is mutually exclusive with the others to prevent them from interfering with each other
Also with a similar matching sprite, which also replaces the cursor when in edit mode (though oversized)
Besides being separated from the other mouse modes, it also requires a user to open the toolbar and select the mode before any of the added code is executed.
This should mean there is no impact on performance, nor any noticeable change, for anyone not using these features.

#### Editable prop for traces
design change from using "local PVs" to instead having "editable" copies-  this stuff isn't limited by what this code determines, so users of the widget can determine where the PVs come from, how they're handled etc. (basically i can add a Save Changes button, but that's not necessarily something people have to use to get the mouse-interaction-changes-points functionality)
It's an additional prop, but also has a default `False` value which means it will not affect any existing usage of `XYPlot` in Phoebus screens, and require no changes to function as before. (Editability is opt-in only)

#### Mouse interaction
Compares cursor pos against coordinates in screen space (so min interaction distance isn't reliant on calculations from plot axis scales)
Moved code for mouse interaction and tracking to a separate class, `PlotEdit.java`, which stores cursor position,
Correctly identifies drag-and-drop mouse interaction, and tracks position during this process. (All that has to be done is to change the corresponding trace point's value to this)

#### Knowledge Gathering
A lot of time spent "Figuring out" the Phoebus codebase, as there wasn't any experience in it (or Java) in the team prior to me joining. A collection of information gathered is in the "knowledge Sharing" section at the end.
This contains info on project structure, functions of separate files, what data is accessed where, and how the work done on this project has been implemented into the existing system.


## Next steps

### **Current design/plan for rest of in-scope work (maybe rework into other areas)**
Please also refer to the [GitHub issue](https://github.com/ControlSystemStudio/phoebus/issues/3167) on the Phoebus repository for the feature's proposal

#### Structure
This was originally planned to be a separate widget entirely, though inheriting behaviour from `XYPlot`, and simply adding the necessary features on top.
This was changed to instead be added features to the existing `XYPlot` widget, with mouse interaction being handled within the base `Plot` class.
Still some concerns about new functionality being triggered when it whouldn't be available, potentially causing errors.
For example, plots can also be used to draw images on an axis (such as heatmaps), but the editing functionality is only intended for `XYPlot`/`RTPlot` usage. This may require further checks to block functionality if incompatible data types are being used.
Changes will be necessary within `XYPlotWidgetRuntime` so that the PV's values can be changed.

#### Mouse Interaction & selecting points
The class which handles existing mouse interaction is `Plot.java`, as it is closest layer to the base javafx.
Additional mouse interaction with the plot is to be added here, with the information and triggers passing upwards towards the XYPlot widget layer.
The data storage and point selection algorithm have been moved to a separate file `PlotEdit.java` to aid ease of development and separate functionality where logical.

Mouse position in screen-space was already available, so when a click is made in Edit Mode, it is compared to each point on each editable trace, converted from a plot-space to screen-space coordinate.
The point with the shortest distance is then checked if it is within the defined "minimum interaction distance" (if a point is too far away, the user won't have intended to select that point), and if so that selection is saved.

A check for the new Edit Mode has also been added to the existing mouse interaction (mouse down, up and move) code, so that point-editing behaviour can be added to these areas.
There are specific checks that can determine when a point is clicked, when the mouse is dragged, and when the point is released, and the position of the cursor in plot-space is updated throughout this.


#### PV editing and Local PVs
Originally, it was planned to have local (as in, PVs with `loc://`) copies of "live" PVs, in order to be able to make changes without immediately applying it to a potentially currently-used PV, while still providing something that can be plotted on the graph.
However, due to the way local PVs work, and how PVs work in Phoebus' runtime, this method did not seem to be possible; the type of a PV must be known in advance, and if a PV has not yet been received/fetched, the type is not known, therefore a copy cannot be made.
A potential workaround was to have a script make the copied PVs in runtime once the originals were fetched, but this would be a lot more complex, and still require integrating into the plot's functioning.
A discussion on this topic was had at: https://github.com/ControlSystemStudio/phoebus/discussions/3213, but it resulted in realising that dynamically creating local PV copies would be infeasible, or require too many workarounds.

The current design for this instead adds a new "Editable" property for traces added to a plot, which is a flag that allows its points to be moved via mouse interaction when in edit mode.
New PVs are added by a user, specifically for use within Phoebus,
This avoids potential over-coupling of behaviour, requires smaller, less intrusive changes, and integrates the new behaviour more seamlessly into the existing features in Phoebus.

#### Save/Edit/Undo buttons
As the Editing behaviour has been changed in the design, this is now slightly separate, and won't be a specific part of the proposed widget anymore.

"Saving changes" will simply be transferring the values of the editable PV copy to the original, and cancelling is the reverse (apply original values to edited copy).

"Undo" functionality in any case requires storing old sets of values so that they can be reverted to. This may need a new list-of-lists PV, so a script can revert values to the next most recent copy, beyond just the most recent change.
Additionally, some consideration will need to be made for what constitutes a "change" that can be undone, as a trace's PV will update multiple times within one drag-and-drop operation.
This could be defined by changes to the local copy PV only occurring when the mouse is released.

### **What is currently in progress?**

#### Updating Pvs with mouse interaction
Working off the existing implemntation for the PlotMarker, which also uses mouse interaction to change a PV from runtime, and drawing new content on the Plot as a result.
If the value in runtime is updated, this will trigger a listener which is comparing its value against its corresponding PV. If they do not match, the change is then applied to the PV.
Likewise, if a change is made to the PV, it will then update the runtime value.
This also triggers a redraw of the graph, meaning the marker will be at the mouse's new position.

Similarly, the movement of the mouse, in edit mode, should change the value of a point to the cursor position's corresponding value.
For testing purposes, this currently triggers an update for marker position, which means it is redrawn at the position of the cursor on the X axis. This, unusually, does not cause a change to the marker's PV.
However, the normal mouse dragging of the marker does update the PV, indicating there is a step missing in replicating this process.
Once each required step for updating the marker PV has been identified, a matching set can be produced for the traces themselves, so that a value change occurs with edit mode interactions, and this updates the origial PV too

As the Marker is only a decimal/ `Double` value, it can be easily compared with the value in its PV, whereas the differences between two lists will require more steps to identify, therefore triggering updates on a detected change between the PV lists may not be feasible.
An alternative could be to have another flag or value to identify when an edit is being made, and to therefore continuously update the PV with the original until it is complete (two-way updates will not be relevant to this situation, as the changes can only be made by the Phoebus edit mode).
This could also be only triggered when the mouse is released, but still making the changes to the trace in runtime, so the responsiveness of the interface isn't affected, but there aren't as many calls being made to update the PV.

### **What can be done next, within this project's scope?**

#### "Save" and "Cancel" buttons
These would be separate to the work on the widget itself, as all these buttons need to do is copy one list PVs value onto another; it is provided an "Original PV" and "Edited PV", and copies values from one to the other accordingly.
It's possible that these could be specific widgets, but all that may need to be contributed back to Phoebus is information on how to implement them.
#### "Undo" button
Assuming this only retains the most recent (or most recent few) changes in the same session, this could be done reasonably within the framework of the changes.
In the same way ongoing edits are tracked, the indexes for Axis, Trace and Value of previous changes would be kept, which are then used to restore the original value if desired.
These would only be retained within the same session, as it would not be stored externally.
#### Axis locking
With current plans only the Y values of points can be changed, due to the way data is stored and used for plots.
Many do not have specific X/Y pairings, so th
The current way the system works only really permits the changing of Y values, since many input datasets do not have X/Y pairings, instead using position in sequence to match a trace value's Y to an X.
Editing an X value for these would be much more complicated, and could move other points in other traces along with it, therefore it would be best to keep both as separate modes.
#### Known Issues
- See above for issue with changes to marker not updating PV when triggered with edit mode mouse interaction (only needed for purposes of understanding PV updates so Trace data can be changed)
- Edit mode cursor is larger than other image cursors, which could obscure too much of the plot

#### Refactoring & Code Cleanup
- Removing debug messages
- Adding further documentation, doc blocks for classes and methods added in the project
- Renaming `PlotEdit.java` to a more descriptive/relevant name, such as `EditModeHandler.java` or similar

### **How can the other requirements / desired features be done, and what challenges may impede them?**
#### Graph interpolation, adding in-between points and applying functions
Though scripts aren't recommended to be run within Phoebus itself, the IOC can run defined actions and python scripts if desired.
New simple boolean flag PVs could be added that are set to `true` by buttons on Phoebus, which then trigger scripts to run on the IOC.
Since this will have access to a more up-to-date version of Python, mathematical modules/packages can be used for these data operations.

#### Archiving changes
This would be a significant undertaking if it is to cover all PVs. Even for a selection, it could easily cause a massive increase in used storage space.



## Knowledge Sharing
### Project Structure / Widget composition
The Phoebus codebase is quite large and complex, meaning it can be difficult to understand the class hierarchy, the relation between the different layers, and what each class is responsible for.

In the case of the XYPlot widget (the focus of this project):
- The widget itself has a Model(`XTPlotWidget`), Runtime(`XYPLotWidgetRuntime`) and Representation(`XYPlotWidgetRepresenatation`)  The Representation uses an:
- `RTValuePlot`, for creating a UI element to plot values, specifically numbers, on a graph - an extension of:
- `RTPlot`, a base implementation / generic form of a Graph/Plot UI element, to wrap
- `Plot`, This contains draw instructions for the plot, and is the layer directly interfacing with user interaction (toolbar mouse modes)

### Widget Model, Runtime and Representation
Making up the upper level of the Phoebus project, each widget is composed of 3 main parts. Model, Runtime and Represenation.
These are meant to handle the separate aspects of a widget's functioning, so that it is easier to maintain, and keep the access and usage of data logically segmented.
These are stored in separate directories, but follow matching name structures:
- `phoebus\app\display\model\src\main\java\org\csstudio\display\builder\model\` For Models (`<name>Widget.java`)
- `phoebus\app\display\runtime\src\main\java\org\csstudio\display\builder\runtime\` For Runtimes (`<name>WidgetRuntime.java`)
- `phoebus\app\display\representation-javafx\src\main\java\org\csstudio\display\builder\representation\javafx\` For Represenations (`<name>WidgetRepresentation.java`)

#### Model (eg: `XYPlotWidget.java`)
Responsible  solely for defining and setting up the data/properties used by the widget - its *model*. There isn't any complex logic here, or any actions beyond setting and accessing data.
This can include some processing of the data however, such as for `XYPlot`, where legacy input is converted into a newer format, so older usages can remain compatible.
This, however, still does not breach the primary rule of the model having no knowledge of how it is used - it doesn't "do" anything.

#### Runtime (eg: `XYPlotWidgetRuntime.java`)
This handles the connection of the PVs to the Properties, and responds to changes in either during the application's *runtime*.
This does not have any knowledge or responsibility for what is displayed or how, simply the passing of data between the PVs and the model.
On initialisation, runtime actions are added, then on start, the PVs are bound to the widget's properties.
They inherit from a base `WidgetRuntime`, which includes all the base-level functions each widget requires, then the inheriting runtimes add their own bindings and functions on top.

#### Representation (eg: `XYPlotWidgetRepresentation.java`)
This handles the actual content the widget is displaying - What is being put onto the GUI, both what is being drawn, but also any user interaction if present.
This basically means all the things related to the displayed content of the widget, what it does when data/props are updated, as well as adding listeners and handlers for user interaction.
This doesn't require any knowledge/interaction with the PVs, since all the representation needs is the properties in the widget's Model. The Runtime handles changes between Model and PV.

### Explanation of files changed
This section is essentially a more detailed set of documentation for the code added. 
Code documentation (potentially contributed back to Phoebus devs) - code itself is reasonably documented, but the overall structure of the project, what's involved in what, and just the general "what's going on" isn't easy to read from the codebase from scratch.

#### `Plot.java` for mouse interaction
This is the "base" point of user interaction with the plot ui element. It has listeners and handling for mouse interaction, implements the toolbar's different mouse modes, and draws the graph ui element.
Initially all the new mouse interaction code was added here, but it has since been moved to methods in `PlotEdit.java`

XYPlotRepresentation uses `RTValuePlot`, an extension of `RTPlot` that specifically uses `Double` values on the X axis, which in itself has a `Plot` instance variable that it wraps the functions of.

#### `PlotEdit.java` edit mode handler object (name subject to change)
This is a new class added for handling the new mouse interaction included with Point Editing.
The stored/tracked data for the point-editing behaviour is:
- Index of selected point's axis (multiple y-indexes can be present)
- Index of selected point's trace in said axis (this can then be used for lookup and accessing trace data)
- Index of selected point within the trace (the data at this index can then be replaced with the new position)
- X and Y coordinates, in graph-space, of the new position (this is what the selected point in the PV would be updated to )

There are two primary reasons for this separation. The function which finds the closest editable point to the cursor is quite large and has many nested loops. Moving this to a separate file means it doesn't fill up the already large `Plot.java` class.
Additionally, since all the internal variables in the `Plot` class are protected, having all the edit-mode-related values in a single object would mean only one getter (for PlotEdit) would be necessary to get the edit mode info at upper layers. This also therefore doesn't expose any "active" information about the interface, so it isn't in contradiction with the coding standards in the same files.
PlotEdit file and what the things it needs to track are (selections, indexes, what the prox calculation and selection function is for)

#### `ToolbarHandler.java`
A new `EDIT` mouse mode was necessary to safely separate the different types of existing mouse interaction from the new point-drag-and-drop being added in this project.
It is part of the same Radio selection (only-one-selected-at-a-time) as the other modes, which means editing cannot be possible while other modes are using mouse input.

A new sprite was added for the Edit Mode button, which is also used for replacing the cursor when editing, to make it clear the mode has been enabled.

New messages also had to be added for this, and the Editable property, so they'd have text appear when hovered over (with french translations, just to be sure it doesn't break from a missing reference)

#### `Trace.java` adding the Editable property
The class for storing and accessing data surrounding the graph lines and points drawn on the Plot. A new boolean property `editable` has been added as a flag so that the point-selecting algorithm knows which traces to ignore. It iterates over all the traces in each axis, but will only compare cursor position against points in traces marked as editable.

Since the property was added to the `Trace` class, all other classes and methods which interacted with (set up, changed and accessed) trace properties had to be updated to handle this new property.
As a result, `PlotWidgetProperties` and `XYPlotRepresentation` had to have additions to handle the new property.
Since `Trace` is an abstracted interface, the implementation, `TraceImpl` also had to be updated to include relevant methods for handling the `editable` property.

Accessing a trace's data (the actual list of `Double` typed Y values, in the case of `XYPlot`) from here is only possible with single random-access, so obtaining a whole copy at once is complicated, especially considering the multithread/locking behaviour used to control data access/usage in a complex GUI like phoebus.
Colours and styling are defined here, so it's possible the line style or colour could be changed when an edit is occurring, then replacing with the original once complete. This would make it more clear which trace is being edited, and when an edit is being made.


### Local Development Setup (docker stuff, IntelliJ)
Current steps that seem to work:
- Pull local copy of Phoebus (from the fork https://github.com/JonFijalkowski/phoebus.git)
- Using [Intellij](https://www.jetbrains.com/idea/download/?section=windows) (community edition works just fine), load up the Phoebus project
- Follow relevant steps on Phoebus' README file for IntelliJ development
- Sign in to Harbor (may not be necessary for locally-hosted containers for PVs and testing)
- Start up relevant docker containers on VSCode (Docker plugin) - such as `Tune-Control-Epics` [Link](https://gitlab.stfc.ac.uk/isis-accelerator-controls/playground/kathryn/tune-control-epics), which is what has been used for testing and development purposes.
- Run the "Phoebus" application run config (top right of Intellij), which should compile the project and run it. Debug messages will appear in the same console that pops up.
- If using breakpoints for debugging purposes, use the Debug button next to the "Play" button instead.

### Useful resources & Links
- Phoebus ReadTheDocs page: https://control-system-studio.readthedocs.io/en/latest/
- Project's fork of Phoebus: https://github.com/JonFijalkowski/phoebus/tree/master
- Issue/Proposal on Phoebus Repo: https://github.com/ControlSystemStudio/phoebus/issues/3167 (requires updating)
- Discussion of writing to NTTable PVs (similar concepts to writing to list PVs the Edit Mode will need to do): https://github.com/ControlSystemStudio/phoebus/issues/1214
- Discussion and implementation of a new Widget from scratch, which gives some info on how widgets are implemented and creation of new interface elements: https://github.com/ControlSystemStudio/phoebus/discussions/2718, https://github.com/ControlSystemStudio/phoebus/pull/2745/files
