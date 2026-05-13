BIN := meo-3-neo-ble-service
TARGET ?= aarch64-unknown-linux-gnu
CROSS ?= cross
CARGO ?= cargo

.PHONY: help build build-arm64 build-aarch64 check check-arm64 test clean run print-artifact

help:
	@echo "Targets:"
	@echo "  make build          Build for the host with cargo"
	@echo "  make build-arm64    Build release binary for ARM64/aarch64 with cross"
	@echo "  make build-aarch64  Alias for build-arm64"
	@echo "  make check          Check for the host with cargo"
	@echo "  make check-arm64    Check ARM64/aarch64 target with cross"
	@echo "  make test           Run host tests with cargo"
	@echo "  make clean          Remove Cargo build output"
	@echo ""
	@echo "Variables:"
	@echo "  TARGET=$(TARGET)"
	@echo "  CROSS=$(CROSS)"

build:
	$(CARGO) build

build-arm64:
	$(CROSS) build --target $(TARGET) --release
	@$(MAKE) print-artifact TARGET=$(TARGET)

build-aarch64: build-arm64

check:
	$(CARGO) check

check-arm64:
	$(CROSS) check --target $(TARGET)

test:
	$(CARGO) test

clean:
	$(CARGO) clean

run:
	$(CARGO) run

print-artifact:
	@echo "Built artifact: target/$(TARGET)/release/$(BIN)"
