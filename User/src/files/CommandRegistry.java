import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CommandRegistry extends CommandParser {
    public CommandRegistry(){
        //команды для управления пользователем
        registerCommand(
                "user-list",
                "вывести список всех пользователей",
                ((scanner, system) -> {
                    String params = "";

                    if(scanner.hasNextLine()){
                        params = scanner.nextLine().trim();
                    }

                    List<User> result = new ArrayList<>();
                    List<User> allUsers = system.getUserManager().findAll();

                    if (params.isBlank()){
                        result = allUsers;
                    } else {

                        String[] filters = params.split(" ");

                        for (User user : allUsers ) {

                            boolean matches = true;

                            for (String f : filters) {
                                if (f.startsWith("username=")) {
                                    String value = f.substring(9);
                                    if (!user.getUsername().equalsIgnoreCase(value)) {
                                        matches = false;
                                        break;
                                    }
                                }

                                if (f.startsWith("email=")) {
                                    String value = f.substring(6);
                                    if (!user.getEmail().equalsIgnoreCase(value)) {
                                        matches = false;
                                        break;
                                    }
                                }
                            }

                            if (matches) {
                                result.add(user);
                            }
                        }
                    }
                    if (result.isEmpty()) {
                        System.out.println("Пользователи не найдены.");
                    } else {
                        printTable(result);
                    }
                })
        );
        registerCommand(
                "user-create",
                "создать нового пользователя",
                ((scanner, system) -> {
                    try {
                        System.out.println("Введите username пользователя");
                        String newUsername = scanner.nextLine();

                        System.out.println("Введите fullname пользователя");
                        String newFullname = scanner.nextLine();

                        System.out.println("Введите email пользователя");
                        String newEmail = scanner.nextLine();

                        User newUser = User.create(newUsername, newUsername, newEmail);

                        system.getUserManager().add(newUser);

                        System.out.println("Пользователь успешно создан");
                    }
                    catch (IllegalArgumentException e){
                        System.out.println("Ошибка: " + e);
                    }
                })
        );
        registerCommand(
                "user-view",
                "просмотр информации о пользователе",
                ((scanner, system) -> {
                    System.out.println("Введите username для просмотра информации");
                    String username = scanner.nextLine();

                    Optional<User> userOpt = system.getUserManager().findByUserName(username);

                    if(userOpt.isPresent()){
                        User user = userOpt.get();
                        boolean hasActiveRoles = false;

                        List<RoleAssignment> assignments = system.getAssignmentManager().findByUser(user);

                        System.out.println("Информация о пользователе: ");
                        System.out.println("Username: " + user.getUsername());
                        System.out.println("Fullname: " + user.getFullname());
                        System.out.println("Email: " + user.getEmail());

                        for (RoleAssignment assignment : assignments){
                            if(assignment.isActive()){
                                hasActiveRoles = true;
                                Role role = assignment.role();
                                System.out.println("Role: " + role.format());
                                System.out.println("Permissions: ");
                                for (Permission perm : role.getPermissions()){
                                    System.out.println(perm.format());
                                }
                            }
                        }

                        if (!hasActiveRoles) {
                            System.out.println("У пользователя нет активных ролей.");
                        }

                    } else {
                        System.out.println("Пользователь с " + username + "не найден");
                    }
                })
        );
        registerCommand(
                "user-update",
                "обновить данные пользователя",
                (scanner, system) -> {
                    System.out.println("Введите username для обновления информации");
                    String username = scanner.nextLine().trim();

                    Optional<User> userOpt = system.getUserManager().findByUserName(username);

                    if (userOpt.isPresent()) {
                        System.out.println("Введите новый fullname для пользователя");
                        String newFullname = scanner.nextLine();

                        System.out.println("Введите новый email для пользователя");
                        String newEmail = scanner.nextLine();

                        try {
                            system.getUserManager().update(username, newFullname, newEmail);
                            System.out.println("Пользователь " + username + " успешно изменен");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Ошибка обновления: " + e.getMessage());
                        }

                    } else {
                        System.out.println("Пользователь с username '" + username + "' не найден");
                    }
                }
        );
        registerCommand(
                "user-delete",
                "удалить пользователя",
                (scanner, system) -> {
                    System.out.println("Введите username для удаления пользователя");
                    String username = scanner.nextLine().trim();

                    Optional<User> userOpt = system.getUserManager().findByUserName(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("Пользователь с username '" + username + "' не найден");
                        return;
                    }

                    System.out.println("Вы точно хотите удалить пользователя? Для подтверждения введите 'да'");
                    String agree = scanner.nextLine().toLowerCase(Locale.ROOT).trim();

                    if (agree.equals("да")) {
                        boolean removed = system.getUserManager().remove(userOpt.get());
                        if (removed) {
                            System.out.println("Пользователь '" + username + "' успешно удалён");
                        } else {
                            System.out.println("Не удалось удалить пользователя '" + username + "'");
                        }
                    } else {
                        System.out.println("Удаление отменено");
                    }
                }
        );
        registerCommand(
                "user-search",
                "поиск пользователей с фильтром",
                (scanner, system) -> {

                    UserFilters filters = new UserFilters();

                    System.out.println("Выберите фильтр:");
                    System.out.println("1 — username");
                    System.out.println("2 — email");
                    System.out.println("3 — домен email");
                    System.out.println("4 — полное имя");

                    String choice = scanner.nextLine().trim();
                    System.out.println("Введите строку для поиска:");
                    String search = scanner.nextLine().trim();

                    UserFilter filter;
                    switch(choice) {
                        case "1":
                            filter = filters.byUsernameContains(search);
                            break;
                        case "2":
                            filter = filters.byEmail(search);
                            break;
                        case "3":
                            filter = filters.byEmailDomain(search);
                            break;
                        case "4":
                            filter = filters.byFullNameContains(search);
                            break;
                        default:
                            System.out.println("Неверный выбор фильтра");
                            return;
                    }

                    List<User> result = system.getUserManager().findByFilter(filter);

                    if(result.isEmpty()) {
                        System.out.println("Пользователи не найдены");
                    } else {
                        System.out.println("Найденные пользователи:");
                        for(User user : result) {
                            System.out.println(user.format());
                        }
                    }
                }
        );

        //команды для управления ролями
        registerCommand(
                "role-list",
                "вывести список всех ролей",
                (scanner, system) -> {
                    List<Role> allRoles = system.getRoleManager().findAll();

                    if (allRoles.isEmpty()) {
                        System.out.println("Роли не найдены.");
                        return;
                    }

                    for (Role role : allRoles) {
                        System.out.println(role.format());
                    }
                }
        );
        registerCommand(
                "role-create",
                "создать новую роль",
                (scanner, system) -> {
                    System.out.println("Введите название роли: ");
                    String roleName = scanner.nextLine().trim();

                    System.out.println("Введите описание роли: ");
                    String descriptionRole = scanner.nextLine().trim();

                    Set<Permission> permissions = new HashSet<>();
                    boolean addingPermissions = true;

                    while (addingPermissions) {
                        System.out.println("Хотите добавить право? (да/нет): ");
                        String answer = scanner.nextLine().trim().toLowerCase();

                        if (answer.equals("да")) {
                            System.out.println("Введите название права (например, READ, WRITE, DELETE): ");
                            String permName = scanner.nextLine().trim();

                            System.out.println("Введите ресурс для права: ");
                            String resource = scanner.nextLine().trim();

                            System.out.println("Введите описание права: ");
                            String permDescription = scanner.nextLine().trim();

                            Permission permission = new Permission(permName, resource, permDescription);
                            permissions.add(permission);
                        } else {
                            addingPermissions = false;
                        }
                    }

                    Role newRole = Role.create(roleName, descriptionRole, permissions);

                    system.getRoleManager().add(newRole);
                    System.out.println("Роль успешно создана:");
                    System.out.println(newRole.format());
                }
        );
        registerCommand(
                "role-view",
                "просмотр роли",
                ((scanner, system) -> {
                    System.out.println("Введите название роли: ");
                    String roleName = scanner.nextLine().trim();

                    Optional<Role> role = system.getRoleManager().findByName(roleName);
                    if(role.isPresent()){
                        Role role1 = role.get();
                        role1.format();
                    } else{
                        System.out.println("Роль с именем " + role + "не найдена");
                    }

                })
        );
        registerCommand(
                "role-update",
                "обновить название и описание роли",
                (scanner, system) -> {
                    System.out.println("Введите название роли для обновления:");
                    String roleName = scanner.nextLine().trim();

                    Optional<Role> roleOpt = system.getRoleManager().findByName(roleName);
                    if (roleOpt.isEmpty()) {
                        System.out.println("Роль не найдена: " + roleName);
                        return;
                    }

                    Role role = roleOpt.get();

                    System.out.println("Введите новое название роли (оставьте пустым, чтобы не менять):");
                    String newName = scanner.nextLine().trim();
                    System.out.println("Введите новое описание роли (оставьте пустым, чтобы не менять):");
                    String newDesc = scanner.nextLine().trim();

                    if (!newName.isBlank()) role.setName(newName);
                    if (!newDesc.isBlank()) role.setDescription(newDesc);

                    System.out.println("Роль обновлена: " + role.format());
                }
        );
        registerCommand(
                "role-delete",
                "удалить роль",
                (scanner, system) -> {
                    System.out.println("Введите название роли для удаления:");
                    String roleName = scanner.nextLine().trim();

                    Optional<Role> roleOpt = system.getRoleManager().findByName(roleName);
                    if (roleOpt.isEmpty()) {
                        System.out.println("Роль не найдена: " + roleName);
                        return;
                    }

                    Role role = roleOpt.get();

                    List<RoleAssignment> assignedUsers = system.getAssignmentManager().findByRole(role);
                    List<RoleAssignment> activeAssignments = new ArrayList<>();

                    for (RoleAssignment roleAssignment : assignedUsers){
                        if (roleAssignment.isActive()) {
                            activeAssignments.add(roleAssignment);
                        }
                    }

                    if (!assignedUsers.isEmpty()) {
                        System.out.println("Роль назначена следующим пользователям:");
                        for (RoleAssignment roleAssignment: assignedUsers) {
                            System.out.println(" - " + roleAssignment.user().format());
                        }
                        System.out.println("Вы уверены, что хотите удалить роль? Введите 'да' для подтверждения:");
                        String confirm = scanner.nextLine().trim().toLowerCase();
                        if (!"да".equals(confirm)) {
                            System.out.println("Удаление отменено.");
                            return;
                        }
                    }

                    system.getRoleManager().remove(role);
                    System.out.println("Роль удалена: " + roleName);
                }
        );

        registerCommand(
                "role-add-permission",
                "добавить право к роли",
                (scanner, system) -> {
                    System.out.println("Введите имя роли: ");
                    String roleName = scanner.nextLine().trim();

                    Optional<Role> roleOpt = system.getRoleManager().findByName(roleName);
                    if (roleOpt.isEmpty()) {
                        System.out.println("Роль не найдена: " + roleName);
                        return;
                    }

                    System.out.println("Введите имя права: ");
                    String permName = scanner.nextLine().trim();

                    System.out.println("Введите имя ресурса: ");
                    String permRes = scanner.nextLine().trim();

                    System.out.println("Введите описание: ");
                    String permDesc = scanner.nextLine().trim();

                    Permission permission = new Permission(permName, permRes, permDesc);
                    system.getRoleManager().addPermissionToRole(roleName, permission);

                    System.out.println("Право добавлено к роли " + roleName);
                }
        );

        registerCommand(
                "role-remove-permission",
                "удалить право из роли",
                (scanner, system) -> {
                    System.out.println("Введите имя роли:");
                    String roleName = scanner.nextLine().trim();

                    Optional<Role> roleOpt = system.getRoleManager().findByName(roleName);
                    if (roleOpt.isEmpty()) {
                        System.out.println("Роль не найдена: " + roleName);
                        return;
                    }

                    Role role = roleOpt.get();
                    List<Permission> perms = new ArrayList<>(role.getPermissions());

                    if (perms.isEmpty()) {
                        System.out.println("У роли нет прав.");
                        return;
                    }

                    System.out.println("Права роли:");
                    for (int i = 0; i < perms.size(); i++) {
                        System.out.printf("%d. %s\n", i + 1, perms.get(i).format());
                    }

                    System.out.println("Введите номер права для удаления:");
                    int index;
                    try {
                        index = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Неверный ввод.");
                        return;
                    }

                    if (index < 0 || index >= perms.size()) {
                        System.out.println("Неверный номер права.");
                        return;
                    }

                    Permission toRemove = perms.get(index);
                    system.getRoleManager().removePermissionFromRole(roleName, toRemove);
                    System.out.println("Право удалено: " + toRemove.format());
                }
        );

        registerCommand(
                "role-search",
                "поиск ролей",
                (scanner, system) -> {
                    System.out.println("Введите фильтры через пробел. Возможные:");
                    System.out.println("nameContains=<строка>  minPermissions=<число>  hasPermission=<имя>:<ресурс>");

                    String line = scanner.nextLine().trim();
                    if (line.isBlank()) {
                        System.out.println("Фильтры не заданы.");
                        return;
                    }

                    String[] filters = line.split("\\s+");
                    List<Role> roles = system.getRoleManager().findAll();

                    for (String f : filters) {
                        if (f.startsWith("nameContains=")) {
                            String value = f.substring(13);
                            List<Role> filteredByName = new ArrayList<>();
                            for (Role r : roles) {
                                if (r.getName().toLowerCase().contains(value.toLowerCase())) {
                                    filteredByName.add(r);
                                }
                            }
                            roles = filteredByName;
                        } else if (f.startsWith("minPermissions=")) {
                            int min;
                            try {
                                min = Integer.parseInt(f.substring(15));
                            } catch (NumberFormatException e) {
                                System.out.println("Неверное значение minPermissions.");
                                return;
                            }
                            int finalMin = min;
                            List<Role> filteredByMinPerms = new ArrayList<>();
                            for (Role r : roles) {
                                if (r.getPermissions().size() >= finalMin) {
                                    filteredByMinPerms.add(r);
                                }
                            }
                            roles = filteredByMinPerms;
                        } else if (f.startsWith("hasPermission=")) {
                            String[] parts = f.substring(14).split(":");
                            if (parts.length != 2) {
                                System.out.println("hasPermission должен быть в формате name:resource");
                                return;
                            }
                            String permName = parts[0];
                            String permRes = parts[1];
                            List<Role> filteredByPermission = new ArrayList<>();
                            for (Role r : roles) {
                                if (r.hasPermission(permName, permRes)) {
                                    filteredByPermission.add(r);
                                }
                            }
                            roles = filteredByPermission;
                        }
                    }

                    if (roles.isEmpty()) {
                        System.out.println("Роли не найдены.");
                    } else {
                        for (Role r : roles) {
                            System.out.println(r.format());
                        }
                    }
                }
        );
        //команды для управления назначениями
        registerCommand(
                "assign-role",
                "назначить роль пользователю",
                (scanner, system) -> {

                    System.out.println("Введите username пользователя:");
                    String username = scanner.nextLine().trim();

                    Optional<User> userOpt = system.getUserManager().findByUserName(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("Пользователь не найден: " + username);
                        return;
                    }
                    User user = userOpt.get();

                    List<Role> allRoles = system.getRoleManager().findAll();
                    if (allRoles.isEmpty()) {
                        System.out.println("Нет доступных ролей для назначения.");
                        return;
                    }

                    System.out.println("Список ролей:");
                    for (int i = 0; i < allRoles.size(); i++) {
                        Role r = allRoles.get(i);
                        System.out.printf("%d. %s (ID: %s, Permissions: %d)%n",
                                i + 1, r.getName(), r.getId(), r.getPermissions().size());
                    }

                    System.out.println("Введите номер роли:");
                    int roleIndex;
                    try {
                        roleIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Неверный формат номера.");
                        return;
                    }

                    if (roleIndex < 0 || roleIndex >= allRoles.size()) {
                        System.out.println("Неверный номер роли.");
                        return;
                    }

                    Role selectedRole = allRoles.get(roleIndex);

                    System.out.println("Тип назначения: 1 - Постоянное, 2 - Временное");
                    String type = scanner.nextLine().trim();

                    String expirationDate = null;

                    if ("2".equals(type)) {
                        System.out.println("Введите дату окончания (yyyy-MM-dd):");
                        expirationDate = scanner.nextLine().trim();

                        try {
                            LocalDate.parse(expirationDate,
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        } catch (Exception e) {
                            System.out.println("Неверный формат даты.");
                            return;
                        }
                    }

                    System.out.println("Введите причину назначения:");
                    String reason = scanner.nextLine().trim();

                    RoleAssignment assignment;

                    if (expirationDate == null) {
                        assignment = new PermanentAssignment(
                                user,
                                selectedRole,
                                AssignmentMetadata.now(reason, system.getCurrentUser())
                        );
                    } else {
                        assignment = new TemporaryAssignment(
                                user,
                                selectedRole,
                                AssignmentMetadata.now(reason, system.getCurrentUser()),
                                expirationDate,
                                true
                        );
                    }

                    try {
                        system.getAssignmentManager().add(assignment);
                        System.out.printf("Роль '%s' назначена пользователю '%s'%n",
                                selectedRole.getName(), user.getUsername());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
        );
        registerCommand(
                "revoke-role",
                "отозвать роль у пользователя",
                (scanner, system) -> {

                    System.out.println("Введите username пользователя:");
                    String username = scanner.nextLine().trim();

                    Optional<User> userOpt = system.getUserManager().findByUserName(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("Пользователь не найден: " + username);
                        return;
                    }

                    User user = userOpt.get();

                    List<RoleAssignment> userAssignments = system.getAssignmentManager().findByUser(user);

                    List<RoleAssignment> activeAssignments = new ArrayList<>();
                    for (RoleAssignment a : userAssignments) {
                        if (a.isActive()) {
                            activeAssignments.add(a);
                        }
                    }

                    if (activeAssignments.isEmpty()) {
                        System.out.println("У пользователя нет активных ролей.");
                        return;
                    }

                    System.out.println("Активные назначения:");
                    for (int i = 0; i < activeAssignments.size(); i++) {
                        RoleAssignment a = activeAssignments.get(i);
                        System.out.printf("%d. %s (%s)%n",
                                i + 1,
                                a.role().getName(),
                                a.assignmentType());
                    }

                    System.out.println("Введите номер назначения для отзыва:");
                    int index;
                    try {
                        index = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Неверный формат номера.");
                        return;
                    }

                    if (index < 0 || index >= activeAssignments.size()) {
                        System.out.println("Неверный номер.");
                        return;
                    }

                    RoleAssignment selected = activeAssignments.get(index);

                    try {
                        system.getAssignmentManager().revokeAssignment(selected.assignmentId());

                        System.out.println("Назначение отозвано.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
        );
        registerCommand(
                "assignment-list",
                "список всех назначений",
                (scanner, system) -> {
                    List<RoleAssignment> allAssignment = system.getAssignmentManager().findAll();

                    if(allAssignment.isEmpty()){
                        System.out.println("Назначений нет");
                        return;
                    }

                    System.out.printf("%-5s | %-10s | %-8s | %-8s | %-8s",
                            "Username", "role", "type", "status", "assigned at");
                    System.out.println("-------------------------------------------------");

                    for (RoleAssignment ra : allAssignment) {
                        System.out.printf("%-5s | %-10s | %-8s | %-8s | %-8s",
                                ra.user().getUsername(),
                                ra.role().getName(),
                                ra.assignmentType(),
                                ra.isActive(),
                                ra.metadata().assignedAt());
                    }
                }
                );
        registerCommand(
                "assignment-list-user",
                "назначения конкретного пользователя",
                (scanner, system) -> {
                    System.out.println("Введите username пользователя");
                    String username = scanner.nextLine().trim();

                    Optional<User> userOpt = system.getUserManager().findByUserName(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("Пользователь не найден: " + username);
                        return;
                    }

                    User user = userOpt.get();

                    List<RoleAssignment> userAssignments = system.getAssignmentManager().findByUser(user);

                    if (userAssignments.isEmpty()) {
                        System.out.println("У пользователя нет назначений.");
                        return;
                    }

                    System.out.println("Назначения пользователя " + username + ":");
                    System.out.println("--------------------------------------------------");

                    for (RoleAssignment ra : userAssignments) {

                        String status = ra.isActive() ? "ACTIVE" : "INACTIVE";

                        System.out.printf(
                                "[%s] %s assigned to %s by %s at %s%nStatus: %s%n",
                                ra.assignmentType(),
                                ra.role().getName(),
                                ra.user().getUsername(),
                                ra.metadata().assignedBy(),
                                ra.metadata().assignedAt(),
                                status
                        );

                        System.out.println("--------------------------------------------------");
                    }
                }
        );
        registerCommand(
                "assignment-list-role",
                "список пользователей с конкретной ролью",
                (scanner, system) -> {

                    System.out.println("Введите имя роли:");
                    String roleName = scanner.nextLine().trim();

                    Optional<Role> roleOpt =
                            system.getRoleManager().findByName(roleName);

                    if (roleOpt.isEmpty()) {
                        System.out.println("Роль не найдена: " + roleName);
                        return;
                    }

                    Role role = roleOpt.get();

                    List<RoleAssignment> assignments =
                            system.getAssignmentManager().findByRole(role);

                    if (assignments.isEmpty()) {
                        System.out.println("Назначений для роли нет.");
                        return;
                    }

                    System.out.println("Пользователи с ролью '" + roleName + "':");

                    for (RoleAssignment a : assignments) {
                        System.out.printf(
                                "- %s (%s)%n",
                                a.user().getUsername(),
                                a.isActive() ? "ACTIVE" : "INACTIVE"
                        );
                    }
                }
        );
        registerCommand(
                "assignment-active",
                "только активные назначения",
                (scanner, system) -> {

                    List<RoleAssignment> active =
                            system.getAssignmentManager().getActiveAssignments();

                    if (active.isEmpty()) {
                        System.out.println("Активных назначений нет.");
                        return;
                    }

                    for (RoleAssignment ra : active) {

                        String status = "ACTIVE";

                        System.out.printf(
                                "[%s] %s assigned to %s by %s at %s%nStatus: %s%n",
                                ra.assignmentType(),
                                ra.role().getName(),
                                ra.user().getUsername(),
                                ra.metadata().assignedBy(),
                                ra.metadata().assignedAt(),
                                status
                        );

                        System.out.println("--------------------------------------------------");
                    }
                }
        );
        registerCommand(
                "assignment-expired",
                "истёкшие временные назначения",
                (scanner, system) -> {

                    List<RoleAssignment> all =
                            system.getAssignmentManager().findAll();

                    boolean found = false;

                    for (RoleAssignment a : all) {
                        if (!a.isActive()
                                && "TEMPORARY".equals(a.assignmentType())) {
                            String status = "ACTIVE";

                            System.out.printf(
                                    "[%s] %s assigned to %s by %s at %s%nStatus: %s%n",
                                    a.assignmentType(),
                                    a.role().getName(),
                                    a.user().getUsername(),
                                    a.metadata().assignedBy(),
                                    a.metadata().assignedAt(),
                                    status
                            );

                            System.out.println("--------------------------------------------------");
                        }
                    }

                    if (!found) {
                        System.out.println("Истёкших назначений нет.");
                    }
                }
        );
        registerCommand(
                "assignment-extend",
                "продлить временное назначение",
                (scanner, system) -> {

                    System.out.println("Введите ID назначения:");
                    String id = scanner.nextLine().trim();

                    Optional<RoleAssignment> opt =
                            system.getAssignmentManager().findById(id);

                    if (opt.isEmpty()) {
                        System.out.println("Назначение не найдено.");
                        return;
                    }

                    RoleAssignment assignment = opt.get();

                    if (!"TEMPORARY".equals(assignment.assignmentType())) {
                        System.out.println("Продлевать можно только временные назначения.");
                        return;
                    }

                    System.out.println("Введите новую дату (yyyy-MM-dd):");
                    String newDate = scanner.nextLine().trim();

                    try {
                        system.getAssignmentManager().extendTemporaryAssignment(id, newDate);

                        System.out.println("Назначение продлено.");
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                }
        );
        registerCommand(
                "permissions-user",
                "все права пользователя",
                (scanner, system) -> {

                    System.out.print("Введите username: ");
                    String username = scanner.nextLine().trim();

                    User user = system.getUserManager().findByUserName(username).orElse(null);

                    if (user == null) {
                        System.out.println("Пользователь не найден.");
                        return;
                    }

                    Set<Permission> permissions = system.getAssignmentManager().getUserPermissions(user);

                    if (permissions.isEmpty()) {
                        System.out.println("У пользователя нет прав.");
                        return;
                    }

                    Map<String, List<Permission>> grouped = new HashMap<>();

                    for (Permission p : permissions) {
                        grouped.computeIfAbsent(p.resource(), k -> new ArrayList<>()).add(p);
                    }

                    for (Map.Entry<String, List<Permission>> entry : grouped.entrySet()) {
                        System.out.println("Resource: " + entry.getKey());
                        for (Permission p : entry.getValue()) {
                            System.out.println("  - " + p.name());
                        }
                    }
                }
        );
        registerCommand(
                "permissions-check",
                "проверить право пользователя",
                (scanner, system) -> {

                    System.out.println("Введите username:");
                    String username = scanner.nextLine().trim();

                    Optional<User> userOpt =
                            system.getUserManager().findByUserName(username);

                    if (userOpt.isEmpty()) {
                        System.out.println("Пользователь не найден.");
                        return;
                    }

                    User user = userOpt.get();

                    System.out.println("Введите имя права:");
                    String permName = scanner.nextLine().trim();

                    System.out.println("Введите ресурс:");
                    String resource = scanner.nextLine().trim();

                    boolean has = system.getAssignmentManager().userHasPermission(user, permName, resource);

                    if (!has) {
                        System.out.println("Право отсутствует.");
                        return;
                    }

                    System.out.println("Право есть. Источник:");

                    List<RoleAssignment> assignments =
                            system.getAssignmentManager().findByUser(user);

                    for (RoleAssignment a : assignments) {
                        if (a.isActive()
                                && a.role().hasPermission(permName, resource)) {

                            System.out.println(
                                    "- роль: " + a.role().getName()
                            );
                        }
                    }
                }
        );
        registerCommand(
                "help",
                "справка по командам",
                (scanner, system) -> {
                    this.printHelp();
                }
        );
        registerCommand(
                "stats",
                "статистика системы",
                (scanner, system) -> {
                    System.out.println(system.generateStatistics());
                }
        );
        registerCommand(
                "clear",
                "очистить экран",
                (scanner, system) -> {
                    for (int i = 0; i < 50; i++) {
                        System.out.println();
                    }
                }
        );
        registerCommand(
                "exit",
                "выход из программы",
                (scanner, system) -> {

                    System.out.println("Вы уверены? (да/нет)");
                    String confirm = scanner.nextLine().trim();

                    if (!"да".equalsIgnoreCase(confirm)) {
                        return;
                    }

                    System.out.println("Завершение программы...");
                    System.exit(0);
                }
        );
        registerCommand("audit-log",
                "просмотр логов",
                (scanner, system) -> {
                    AuditLog auditLog = system.getAuditLog();
                    auditLog.printLog();
                });
    }

    private void printTable(List<User> users) {
        System.out.printf("%-5s | %-10s | %-8s ",
                "Username", "Fullname", "Email");
        System.out.println("\n-------------------------------------------------");

        for (User u : users) {
            System.out.printf("%-5s | %-10s | %-8s \n",
                    u.getUsername(),
                    u.getFullname(),
                    u.getEmail());
        }
    }


}
