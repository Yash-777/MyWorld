package com.github.yash777.streams;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

public class UserMapperTest {

    public static void main(String[] args) {
        // Simulated repository result
        Optional<UserEntity> entityOpt = Optional.of(
                new UserEntity("U001", "Alice", true)
        );

        // Initialize mapper
        UserMapper mapper = new UserMapper();
        // Map to model using method reference
        User model = entityOpt
                .map(mapper::mapToModel)
                .orElse(null);

        // Print result
        System.out.println("Mapped User: " + model);
        
        Optional<User> modelOpt = entityOpt
                  .map(mapper::mapToModel);
        System.out.println("Mapped Optional User: " + modelOpt.isPresent());
        System.out.println("Mapped Optional User: " + modelOpt.get());
        
        // Handle Optional empty scenario (for demo)
        Optional<UserEntity> emptyOpt = Optional.empty();
        //emptyOpt.map(mapper::mapToModel);
        System.out.println("Mapped Optional isPresent: " + emptyOpt.isPresent());
        System.out.println("Mapped Optional isEmpty: " + emptyOpt.isEmpty());
        
     // Handle Optional empty scenario (for demo)
        UserEntity entity = entityOpt.orElse(null);
        //emptyOpt.map(mapper::mapToModel);
        System.out.println("Mapped entity: " + entity);
    }

    // === Entity class ===
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class UserEntity {
        private String userId;
        private String fullName;
        private boolean active;
    }

    // === Model (DTO) class ===
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class User {
        private String id;
        private String name;
        private boolean active;
    }

    // === Mapper class ===
    static class UserMapper {
        public User mapToModel(UserEntity entity) {
        	if (entity == null) return null;
            return new User(
                    entity.getUserId(),
                    entity.getFullName(),
                    entity.isActive()
            );
        }
    }
}
