package com.xentrom.backend.dto.output;

import com.xentrom.backend.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GetUsersOutput extends Output {
    private UserDto[] users;
}
