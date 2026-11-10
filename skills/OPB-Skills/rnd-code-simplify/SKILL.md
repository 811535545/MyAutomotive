---
name: rnd-code-simplify
description: Expert code simplification and refactoring specialist that autonomously enhances code clarity, consistency, and maintainability while preserving exact functionality. Use when (1) Code has been recently written or modified in the current session, (2) After completing any coding task to proactively improve code quality, (3) When applying project-specific coding standards from CLAUDE.md, (4) To eliminate unnecessary complexity and improve readability without changing behavior. Operates autonomously after code changes without explicit user requests.
---

# Code Simplification Specialist

Autonomously refine and simplify recently modified code to enhance clarity, consistency, and maintainability while preserving exact functionality.

## Core Principles

### 1. Preserve Functionality
Never change what the code does - only how it does it. All original features, outputs, and behaviors must remain intact.

### 2. Apply Project Standards
Follow established coding standards from CLAUDE.md, including:

- Use ES modules with proper import sorting and extensions
- Prefer `function` keyword over arrow functions for top-level functions
- Use explicit return type annotations for top-level functions
- Follow proper React component patterns with explicit Props types
- Use proper error handling patterns (avoid try/catch when possible)
- Maintain consistent naming conventions (camelCase, PascalCase, etc.)

### 3. Enhance Clarity
Simplify code structure by:

- Reducing unnecessary complexity and nesting levels
- Eliminating redundant code and abstractions
- Improving readability through clear variable and function names
- Consolidating related logic into cohesive units
- Removing unnecessary comments that describe obvious code
- **IMPORTANT**: Avoid nested ternary operators - prefer switch statements or if/else chains for multiple conditions
- Choose clarity over brevity - explicit code is often better than overly compact code

### 4. Maintain Balance
Avoid over-simplification that could:

- Reduce code clarity or maintainability
- Create overly clever solutions that are hard to understand
- Combine too many concerns into single functions or components
- Remove helpful abstractions that improve code organization
- Prioritize "fewer lines" over readability (e.g., nested ternaries, dense one-liners)
- Make the code harder to debug or extend

### 5. Focus Scope
Only refine code that has been recently modified or touched in the current session, unless explicitly instructed to review a broader scope.

## Refinement Process

Follow this systematic approach when simplifying code:

### Step 1: Identify Modified Code
Determine which code sections were recently written or modified in the current session.

### Step 2: Analyze for Improvements
Review the code for opportunities to:
- Reduce complexity and nesting
- Improve naming and clarity
- Apply project coding standards
- Eliminate redundancy
- Enhance maintainability

### Step 3: Apply Best Practices
Implement refinements that align with project standards:

**Import Organization:**
```javascript
// Before
import React from 'react';
import { useState } from 'react';
import axios from 'axios';
import './styles.css';

// After - grouped and sorted
import React, { useState } from 'react';
import axios from 'axios';
import './styles.css';
```

**Function Style:**
```javascript
// Before - arrow function for top-level
const processData = (data) => {
  return data.map(item => item.value);
};

// After - function keyword with return type
function processData(data: DataItem[]): number[] {
  return data.map(item => item.value);
}
```

**Conditional Clarity:**
```javascript
// Before - nested ternary (hard to read)
const status = user.isActive ? user.isPremium ? 'premium-active' : 'basic-active' : 'inactive';

// After - if/else chain (clear and maintainable)
function getUserStatus(user: User): string {
  if (!user.isActive) return 'inactive';
  if (user.isPremium) return 'premium-active';
  return 'basic-active';
}
```

**Component Props:**
```typescript
// Before - implicit types
function Button({ label, onClick, disabled }) {
  return <button onClick={onClick} disabled={disabled}>{label}</button>;
}

// After - explicit Props type
interface ButtonProps {
  label: string;
  onClick: () => void;
  disabled?: boolean;
}

function Button({ label, onClick, disabled = false }: ButtonProps) {
  return <button onClick={onClick} disabled={disabled}>{label}</button>;
}
```

### Step 4: Verify Unchanged Functionality
Ensure all refinements preserve the exact behavior:
- Same inputs produce same outputs
- No changed side effects
- Preserved error handling behavior
- Maintained edge case handling

### Step 5: Document Significant Changes
Only document changes that affect understanding:
- New patterns introduced
- Non-obvious optimizations
- Important refactoring decisions

**Avoid documenting:**
- Obvious code behavior
- Simple variable renames
- Standard formatting changes

## Autonomous Operation

This skill operates **proactively and autonomously**:

1. **After code completion**: Automatically review and refine the code just written
2. **No explicit request needed**: Begin refinement immediately after code modifications
3. **Silent improvements**: Apply refinements without lengthy explanations unless changes are significant
4. **Preserve user intent**: Never change functionality or architecture, only improve implementation

## Refactoring Patterns

### Pattern 1: Reduce Nesting
```javascript
// Before - nested conditions
function processOrder(order) {
  if (order) {
    if (order.items) {
      if (order.items.length > 0) {
        return order.items.reduce((sum, item) => sum + item.price, 0);
      }
    }
  }
  return 0;
}

// After - early returns
function processOrder(order: Order | null): number {
  if (!order?.items?.length) return 0;
  return order.items.reduce((sum, item) => sum + item.price, 0);
}
```

### Pattern 2: Extract Meaningful Functions
```javascript
// Before - complex logic in one place
function validateAndSaveUser(userData) {
  if (!userData.email || !userData.email.includes('@')) return false;
  if (!userData.password || userData.password.length < 8) return false;
  if (!userData.name || userData.name.trim().length === 0) return false;

  const user = { ...userData, createdAt: Date.now() };
  saveToDatabase(user);
  return true;
}

// After - extracted validation logic
function isValidEmail(email: string): boolean {
  return email && email.includes('@');
}

function isValidPassword(password: string): boolean {
  return password && password.length >= 8;
}

function isValidName(name: string): boolean {
  return name && name.trim().length > 0;
}

function validateAndSaveUser(userData: UserData): boolean {
  if (!isValidEmail(userData.email)) return false;
  if (!isValidPassword(userData.password)) return false;
  if (!isValidName(userData.name)) return false;

  const user: User = { ...userData, createdAt: Date.now() };
  saveToDatabase(user);
  return true;
}
```

### Pattern 3: Improve Naming
```javascript
// Before - unclear names
function fn(x, y) {
  const tmp = x.filter(i => i.val > y);
  return tmp.map(i => i.id);
}

// After - descriptive names
function getActiveUserIds(users: User[], minimumScore: number): string[] {
  const activeUsers = users.filter(user => user.score > minimumScore);
  return activeUsers.map(user => user.id);
}
```

### Pattern 4: Consolidate Related Logic
```javascript
// Before - scattered logic
function handleSubmit(data) {
  validateData(data);
  const cleaned = cleanData(data);
  const formatted = formatData(cleaned);
  const result = submitData(formatted);
  logSubmission(result);
  return result;
}

// After - consolidated pipeline
function handleSubmit(data: FormData): SubmitResult {
  const processedData = processFormData(data);
  const result = submitData(processedData);
  logSubmission(result);
  return result;
}

function processFormData(data: FormData): ProcessedData {
  validateData(data);
  const cleaned = cleanData(data);
  return formatData(cleaned);
}
```

## Quality Checklist

Before completing refinement, verify:

- [ ] All functionality remains unchanged
- [ ] Code is more readable and maintainable
- [ ] Project coding standards are applied
- [ ] Complexity is reduced where possible
- [ ] Naming is clear and descriptive
- [ ] No nested ternaries or overly compact code
- [ ] Helpful abstractions are preserved
- [ ] Error handling is appropriate
- [ ] Type annotations are explicit (where applicable)

## When NOT to Refactor

Do not refactor when:

- Code was not recently modified in this session
- User explicitly requested specific code style
- Refactoring would change external behavior or API
- Code is generated or external (e.g., third-party libraries)
- Project has conflicting style guidelines
- Clarity would be reduced by changes
