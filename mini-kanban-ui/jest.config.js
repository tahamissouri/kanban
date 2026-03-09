/** @type {import('jest').Config} */
module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jsdom',
  setupFilesAfterFramework: [],
  moduleFileExtensions: ['ts', 'js', 'json'],
  transform: {
    '^.+\\.ts$': ['ts-jest', {
      tsconfig: 'tsconfig.spec.json',
    }],
  },
  testMatch: ['**/*.spec.ts'],
  moduleNameMapper: {
    '^@angular/core$': '<rootDir>/node_modules/@angular/core',
    '^@angular/common$': '<rootDir>/node_modules/@angular/common',
    '^@angular/router$': '<rootDir>/node_modules/@angular/router',
    '^@angular/common/http$': '<rootDir>/node_modules/@angular/common/http',
  },
  collectCoverageFrom: [
    'src/app/**/*.ts',
    '!src/app/**/*.module.ts',
    '!src/app/**/*.routes.ts',
    '!src/main.ts',
  ],
  coverageReporters: ['text', 'html'],
};
