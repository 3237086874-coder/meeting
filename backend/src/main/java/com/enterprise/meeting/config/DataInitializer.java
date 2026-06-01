package com.enterprise.meeting.config;

import com.enterprise.meeting.entity.*;
import com.enterprise.meeting.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            log.info("Data already initialized, skipping...");
            return;
        }

        log.info("Initializing seed data...");

        List<Role> roles = roleRepository.saveAll(List.of(
                Role.builder().code("SUPER_ADMIN").name("超级管理员").description("系统最高管理角色").build(),
                Role.builder().code("PRESIDENT").name("高管").description("高级管理层").build(),
                Role.builder().code("DEPT_HEAD").name("部门主管").description("部门负责人").build(),
                Role.builder().code("EXECUTOR").name("执行人").description("任务执行人员").build(),
                Role.builder().code("STAFF").name("普通用户").description("普通员工").build()
        ));
        log.info("Created {} roles", roles.size());

        List<Department> depts = departmentRepository.saveAll(List.of(
                Department.builder().name("研发部").description("技术研发部门").build(),
                Department.builder().name("产品部").description("产品管理部门").build(),
                Department.builder().name("市场部").description("市场营销部门").build(),
                Department.builder().name("运营部").description("运营管理部门").build(),
                Department.builder().name("运维部").description("系统运维部门").build(),
                Department.builder().name("人事部").description("人力资源部门").build()
        ));
        log.info("Created {} departments", depts.size());

        String defaultPwd = passwordEncoder.encode("123456");
        List<User> users = userRepository.saveAll(List.of(
                User.builder().username("admin").passwordHash(defaultPwd).displayName("系统管理员")
                        .phone("13800000000").isActive(true).role(roles.get(0)).build(),
                User.builder().username("zhangsan").passwordHash(defaultPwd).displayName("张三")
                        .phone("13800000001").isActive(true).role(roles.get(1)).department(depts.get(0)).title("技术总监").build(),
                User.builder().username("lisi").passwordHash(defaultPwd).displayName("李四")
                        .phone("13800000002").isActive(true).role(roles.get(2)).department(depts.get(0)).title("研发主管").build(),
                User.builder().username("wangwu").passwordHash(defaultPwd).displayName("王五")
                        .phone("13800000003").isActive(true).role(roles.get(3)).department(depts.get(0)).title("高级工程师").build(),
                User.builder().username("zhaoliu").passwordHash(defaultPwd).displayName("赵六")
                        .phone("13800000004").isActive(true).role(roles.get(3)).department(depts.get(0)).title("工程师").build()
        ));
        log.info("Created {} users (default pwd: 123456)", users.size());
    }
}
